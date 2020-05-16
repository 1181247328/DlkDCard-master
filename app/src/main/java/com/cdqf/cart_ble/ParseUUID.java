package com.cdqf.cart_ble;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ParseUUID {

    private static String TAG = ParseUUID.class.getSimpleName();

    public static List<UUID> parseUUIDone(final byte[] advertisedData) {
        List<UUID> uuids = new ArrayList<UUID>();
        int offset = 0;
        while (offset < (advertisedData.length - 2)) {
            int len = advertisedData[offset++];
            if (len == 0)
                break;
            int type = advertisedData[offset++];
            switch (type) {
                case 0x02:
                case 0x03:
                    while (len > 1) {
                        int uuid16 = advertisedData[offset++];
                        uuid16 += (advertisedData[offset++] << 8);
                        len -= 2;
                        uuids.add(UUID.fromString(String.format(
                                "%08x-0000-1000-8000-00805f9b34fb", uuid16)));
                    }
                    break;
                case 0x06:
                case 0x07:
                    while (len >= 16) {
                        try {
                            ByteBuffer buffer = ByteBuffer.wrap(advertisedData,
                                    offset++, 16).order(ByteOrder.LITTLE_ENDIAN);
                            long mostSignificantBit = buffer.getLong();
                            long leastSignificantBit = buffer.getLong();
                            uuids.add(new UUID(leastSignificantBit,
                                    mostSignificantBit));
                        } catch (IndexOutOfBoundsException e) {
                            continue;
                        } finally {
                            offset += 15;
                            len -= 16;
                        }
                    }
                    break;
                default:
                    offset += (len - 1);
                    break;
            }
        }
        return uuids;
    }

    private static final char[] b = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};


    public static List<String> parseUUIDtwo(byte[] var2) {
        List<String> uuids = new ArrayList<String>();
        if ((var2[5] & 255) == 76 && (var2[6] & 255) == 0 && (var2[7] & 255) == 2 && (var2[8] & 255) == 21) {
            int var3 = (var2[25] & 255) * 256 + (var2[26] & 255);
            int var4 = (var2[27] & 255) * 256 + (var2[28] & 255);
            byte var5 = var2[29];
            byte var6 = var2[31];
            byte[] var7 = new byte[16];
            System.arraycopy(var2, 9, var7, 0, 16);
            var2 = var7;
            char[] var11 = new char[var7.length * 2];

            for (int var9 = 0; var9 < var2.length; ++var9) {
                int var8 = var2[var9] & 255;
                var11[var9 * 2] = b[var8 >>> 4];
                var11[var9 * 2 + 1] = b[var8 & 15];
            }

            String var10 = new String(var11);
            StringBuilder var12;
            (var12 = new StringBuilder()).append(var10.substring(0, 8));
            var12.append("-");
            var12.append(var10.substring(8, 12));
            var12.append("-");
            var12.append(var10.substring(12, 16));
            var12.append("-");
            var12.append(var10.substring(16, 20));
            var12.append("-");
            var12.append(var10.substring(20, 32));
            var10 = var12.toString();
            uuids.add(var10);
            return uuids;
        }
        return uuids;
    }

    public static final List<UUID> paresUUIDthree(byte[] data) {
        List<UUID> uuids = new ArrayList<UUID>();
        StringBuilder stringBuilder = new StringBuilder(data.length);
        for (byte byteChar : data) {
            stringBuilder.append(String.format("%02X ", byteChar).trim());
        }
        String var10 = stringBuilder.substring(18, 50);
        StringBuilder var12;
        (var12 = new StringBuilder()).append(var10.substring(0, 8));
        var12.append("-");
        var12.append(var10.substring(8, 12));
        var12.append("-");
        var12.append(var10.substring(12, 16));
        var12.append("-");
        var12.append(var10.substring(16, 20));
        var12.append("-");
        var12.append(var10.substring(20, 32));
        var10 = var12.toString();
        UUID uuid = UUID.fromString(var10);
        uuids.add(uuid);
        return uuids;
    }
}
