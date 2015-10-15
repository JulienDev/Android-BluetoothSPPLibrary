/*
 * Copyright 2014 Akexorcist
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package app.akexorcist.bluetotohspp.library;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.Set;

@SuppressLint("NewApi")
public class DeviceSearcher {

    public interface OnDeviceFoundListener {
        void onDeviceFound(BluetoothDevice device);
    }

    // Member fields
    private Context mContext;
    private BluetoothAdapter mBtAdapter;
    private Set<BluetoothDevice> pairedDevices;
    private OnDeviceFoundListener mOnDeviceFoundListener;

    public DeviceSearcher(final Context context, final OnDeviceFoundListener onDeviceFoundListener) {
        mContext = context;
        final IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        context.registerReceiver(mReceiver, filter);
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        pairedDevices = mBtAdapter.getBondedDevices();
        mOnDeviceFoundListener = onDeviceFoundListener;
    }

    public void startDiscovery() {
        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (mOnDeviceFoundListener != null) {
                    mOnDeviceFoundListener.onDeviceFound(device);
                }
            }
        }
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }
        mBtAdapter.startDiscovery();
    }

    public void stopDiscovery() {
        if (mBtAdapter != null && mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }
        mContext.unregisterReceiver(mReceiver);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    if (mOnDeviceFoundListener != null) {
                        mOnDeviceFoundListener.onDeviceFound(device);
                    }
                }
            }
        }
    };

}
