package com.heavendevelopment.aranharobotica_ufc;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DispositivosPareadosActivity extends AppCompatActivity {


    ListView lista;
    List<Device> devices;
    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice bluetoothDevice;
    DispositivosPareadosAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispositivos_pareados);

        lista = (ListView) findViewById(R.id.lv_dispositvos_pareados);

        findDevices();

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Device d = devices.get(position);

                //Retorna os dispositivos pareados e seleciona o que foi clicado na ListView
                Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
                if (pairedDevices.size() > 0) {
                    for (BluetoothDevice device : pairedDevices) {
                        if (device.getAddress().equals(d.getMacAddress())){
                            d.setMacAddress(device.getAddress());
                            d.setName(device.getName());
                            bluetoothDevice = device;
                            break;
                        }
                    }
                }

                Intent i = new Intent(DispositivosPareadosActivity.this, MainActivity.class);
                Bundle params = new Bundle();
                params.putBoolean("status", true);
                i.putExtra("device", d);
                i.putExtras(params);
                DispositivosPareadosActivity.this.startActivity(i);
                DispositivosPareadosActivity.this.finish();
            }

        });
    }

    //Busca dispositivos já pareados

    void findDevices() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {

            devices = new ArrayList<Device>();

            for (BluetoothDevice device : pairedDevices) {

                devices.add(new Device(device.getName(), device.getAddress()));
            }

            // Add o nome e endereco no ArrayAdapter para ser visualizado na ListView
            adapter = new DispositivosPareadosAdapter(this, devices);
            lista.setAdapter(adapter);

        }
    }

}
