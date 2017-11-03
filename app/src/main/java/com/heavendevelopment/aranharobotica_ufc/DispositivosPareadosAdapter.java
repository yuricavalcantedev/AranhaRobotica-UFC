package com.heavendevelopment.aranharobotica_ufc;

import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Yuri on 03/11/2017.
 */

public class DispositivosPareadosAdapter extends ArrayAdapter<Device> {

    Context context;
    List<Device> devices;

    public DispositivosPareadosAdapter(Context context, List<Device> dev) {
        super(context, 0);
        this.context = context;
        this.devices = dev;
    }

    @Override
    public int getCount() {
        return devices.size();
    }

    @Override
    public Device getItem(int position) {
        return devices.get(position);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_dispositivo_pareado, null);
        }

        TextView tvName = (TextView) view.findViewById(R.id.tv_name);
        TextView tvMacAddress = (TextView) view.findViewById(R.id.tv_mac_adress);

        Device device = devices.get(position);

        tvName.setText(device.getName());
        tvMacAddress.setText(device.getMacAddress());

        return view;
    }

}
