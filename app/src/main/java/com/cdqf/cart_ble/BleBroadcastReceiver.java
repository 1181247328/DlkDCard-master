package com.cdqf.cart_ble;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import de.greenrobot.event.EventBus;

public class BleBroadcastReceiver extends BroadcastReceiver {

    private String TAG = BleBroadcastReceiver.class.getSimpleName();

    private EventBus eventBus = EventBus.getDefault();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                    BluetoothAdapter.ERROR);
            switch (state) {
                case BluetoothAdapter.STATE_OFF:
                    Log.e(TAG, "STATE_OFF 手机蓝牙关闭");
                    eventBus.post(new BleCloseFind());
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    Log.e(TAG, "STATE_TURNING_OFF 手机蓝牙正在关闭");
                    break;
                case BluetoothAdapter.STATE_ON:
                    Log.e(TAG, "STATE_ON 手机蓝牙开启");
                    eventBus.post(new BleOpenFind());
                    break;
                case BluetoothAdapter.STATE_TURNING_ON:
                    Log.e(TAG, "STATE_TURNING_ON 手机蓝牙正在开启");
                    break;
            }
        }
    }
}
