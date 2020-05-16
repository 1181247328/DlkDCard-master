package com.cdqf.cart_ble;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.ScanRecord;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;


import com.cdqf.cart_state.CartState;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import de.greenrobot.event.EventBus;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BleService extends Service {

    private String TAG = BleService.class.getSimpleName();

    private CartState direState = CartState.getCartState();

//    public final static UUID UUID_EBOYLIGHT_LED = UUID.fromString("fda50693-a4e2-4fb1-afcf-c6eb07647825");

    public final static UUID UUID_EBOYLIGHT_LED = UUID.fromString("FDA50693-A4E2-4FB1-AFCF-C6EB07647825");

    //密码认证通道
    public final static UUID UUID_HAND_CHAR = UUID.fromString("00001804-0000-1000-8000-00805f9b34fb");

    //灯泡颜色状态数握
    public final static UUID UUID_COLOR_READ = UUID.fromString("0000ee01-0000-1000-8000-00805f9b34fb");

    private Context mContext = null;

    private EventBus eventBus = EventBus.getDefault();

    private CartState state = CartState.getCartState();

    //连接数量
    private int BLUETOOTH_CONNECT_NUMBER = 0;

    //当前连接的数量
    private int size = 0;

    //确何蓝牙设备不会重复
    private Map<String, Boolean> gattMarryMap = new Hashtable<String, Boolean>();

    private List<String> bleList = new CopyOnWriteArrayList<>();

    //保存gatt
    private Map<String, BluetoothGatt> gattMap = new Hashtable<String, BluetoothGatt>();

    private BluetoothGattCharacteristic characteristic = null;

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            super.onPhyUpdate(gatt, txPhy, rxPhy, status);
        }

        @Override
        public void onPhyRead(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            super.onPhyRead(gatt, txPhy, rxPhy, status);
        }

        //连接状态的改变
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            //连接没有超时
            //获得蓝牙远程设备的地址
            final String address = gatt.getDevice().getAddress();

            Log.e(TAG, "---" + address + "---" + status + "---" + newState);

            //判断这个连接是不是假连接
            boolean isSuccess = ((status == BluetoothGatt.GATT_SUCCESS) && (newState == BluetoothProfile.STATE_CONNECTED));

            if (isSuccess) {
                Log.e(TAG, "---" + address + "---onConnectionStateChange连接正常");
                discoverServices(address);
            }
        }

        //查找Services,连接成功就返回
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            String address = gatt.getDevice().getAddress();
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.e(TAG, "---" + address + "---onServicesDiscovered连接服务成功");
                if (!handDevice(address)) {
                    Log.e(TAG, "---" + address + "---onServicesDiscovered写入失败");
                } else {
                    Log.e(TAG, "---" + address + "---onServicesDiscovered写入成功");
                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        //BLE设备返回数据时触发
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            //获得通知更新成功的BLE中的MAC地址
            String address = gatt.getDevice().getAddress();

            Log.e(TAG, "---" + address + "---onCharacteristicChanged通知更新成功");
        }

        //读数据的时候会触发
        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            String address = gatt.getDevice().getAddress();
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.e(TAG, "---" + address + "---onCharacteristicRead数据读取正常");
            } else {
                Log.e(TAG, "---" + address + "---onCharacteristicRead数据读取异常");
            }
        }

        //发数据触发
        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            String address = gatt.getDevice().getAddress();
            //如果status为0的话就是成功,不为0那么就是失败,失败就断开连接
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.e(TAG, "---" + address + "---onCharacteristicWrite数据写入正常");
                characteristic = getReadCharacteristic(address);

                //通知设备更新,true是打开的意思
                boolean isGattNotification = gatt.setCharacteristicNotification(characteristic, true);

                if (isGattNotification) {
                    Log.e(TAG, "---" + address + "---onCharacteristicWrite通知更新成功");
                }
            }
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
        }
    };

    public void onEventMainThread(DeleteFind d) {
        gattMarryMap.clear();
    }

    @SuppressLint("MissingPermission")
    public void onEventMainThread(DeviceFind d) {
        if (state.getmBluetoothAdapter().isEnabled()) {
            //获得灯的地址
            String address = d.device.getAddress();

            //获得连接数量
            size = gattMarryMap.keySet().size();

//            //判断是不是大于连接数据
//            if (size > BLUETOOTH_CONNECT_NUMBER) {
//                return;
//            }
            //判断是不是存在了
            if (gattMarryMap.get(address) != null && gattMarryMap.get(address)) {
                return;
            }

            Log.e(TAG, "---设备名称---" + d.device.getName() + "---地址---" + d.device.getAddress());

            //保存为true
            gattMarryMap.put(address, true);

            bleList.add(address);

            direState.setBleList(bleList);

            eventBus.post(new BleFind(bleList));

            //开始连接
            connect(address);
        } else {
            Log.e(TAG, "---蓝牙关闭---");
        }
    }

    //5.0以上处理
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void onEventMainThread(DeviceResult r) {
        BluetoothDevice device = r.result.getDevice();
        ScanRecord record = r.result.getScanRecord();
        bleDevice(device, record.getBytes());
    }

    @SuppressLint("MissingPermission")
    private void bleDevice(BluetoothDevice device, byte[] record) {
        Log.e(TAG, "---" + device.getName() + "---" + device.getAddress());
        List<UUID> uuids = ParseUUID.parseUUIDone(record);
        Log.e(TAG, "---" + uuids.size());
        for (UUID uuid : uuids) {
            Log.e(TAG, "---" + uuid.toString());
            if (uuid.equals(UUID_EBOYLIGHT_LED)) {
                //发送信息,也就是将一个蓝牙远程设备的信息发送
                Log.e(TAG, "---设备---" + device.getAddress());
                break;
            } else {
                Log.e(TAG, "---" + device.getAddress() + "---不是你当前要找的BLE设备");
            }
        }
    }


    /**
     * 连接请求
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void connect(String addressConnect) {
        //从蓝牙适配器中获得device,也就是说,获得一个远程蓝牙的设备,可以从中获得其基本信息,包括地址 当前状态等等.
        BluetoothDevice device = state.getmBluetoothAdapter().getRemoteDevice(addressConnect);

        //开始连接
        //第一个是你所关联的上下文
        //第二个参数是自动连接,你可以用true,但是一定没有false快
        //第三个参数是一个接口,他会返回一些信息,具体的去看看
        BluetoothGatt gattConnect = device.connectGatt(mContext, false, mGattCallback);

        //如果连接不成功的话,gatt就等于空,那么就从中去旧
        if (gattConnect == null) {
            //从MAP中去掉你所要连接的gatt,当然前提是它存在
            Log.e(TAG, "---" + addressConnect + "---连接GATT通道失败");
            gattMap.remove(addressConnect);
        } else {
            Log.e(TAG, "---" + addressConnect + "---连接GATT通道成功");
            //连接成功就将地址和其所对应的gatt保有存进Map中去
            gattMap.put(addressConnect, gattConnect);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private boolean discoverServices(String address) {
        //获得对应BEL地址的gatt,也就是管道了
        BluetoothGatt gatt = gattMap.get(address);

        //延迟8秒,如果不加延迟的话有可能会出现系统认为连接服务成功,但是却没有回调的情况
        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //寻找服务是否成功,如果成功就会触发那个接口
        boolean ret = gatt.discoverServices();
        return ret;
    }

    //连接GATT 服务成功后
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private boolean handDevice(String address) {
        BluetoothGattCharacteristic handCharacteristic;
        BluetoothGattService LedService;

        //获得通道
        BluetoothGatt gatt = gattMap.get(address);

        //获得Service
        LedService = gatt.getService(UUID_EBOYLIGHT_LED);

        Log.e(TAG, "----1");

        //通过Srvice获得对应的特征值
        handCharacteristic = LedService.getCharacteristic(UUID_EBOYLIGHT_LED);

        if (handCharacteristic == null) {
            Log.e(TAG, "----2");
        } else {
            Log.e(TAG, "----3");
        }

        //获得写入类型后规定writeCharacteristic的写入类型
//        handCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);

        //将配置值写入
        return gatt.writeCharacteristic(handCharacteristic);
    }

    /**
     * 读数据
     *
     * @param address
     * @return
     */
    private BluetoothGattCharacteristic getReadCharacteristic(String address) {
        return getCharacteristic(address, UUID_COLOR_READ);
    }

    private BluetoothGattCharacteristic getCharacteristic(String address, UUID uuid) {
        BluetoothGatt gatt;
        BluetoothGattService LedService;
        BluetoothGattCharacteristic mLedColorCharacteristic;

        //获得BLE的MAC地址
        gatt = gattMap.get(address);

        //获得远程服务所支持的service
        LedService = gatt.getService(UUID_EBOYLIGHT_LED);

        //获得给定服务的特征值
        mLedColorCharacteristic = LedService.getCharacteristic(uuid);
        return mLedColorCharacteristic;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "---服务开始---");
        eventBus.register(this);
        mContext = this;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "---服务销毁---");
        eventBus.unregister(this);
    }

    /**
     * 返回服务
     */
    class BleIbinder extends Binder {
        public Service getService() {
            return BleService.this;
        }
    }
}
