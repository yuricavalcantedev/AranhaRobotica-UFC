package com.heavendevelopment.aranharobotica_ufc;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static android.R.attr.label;

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

        Button bt_search = (Button) findViewById(R.id.bt_search_dispositivos_pareados);
        bt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);

                    adapter.clear();
                    registerReceiver(mReceiver, filter);
                    bluetoothAdapter.startDiscovery();

                    //bluetoothAdapter.cancelDiscovery();

            }
        });


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

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d("DeviceList" , device.getName() + "\n" + device.getAddress());
                Device tempDevice = new Device();
                tempDevice.setMacAddress(device.getAddress());
                tempDevice.setName(device.getName());
                devices.add(tempDevice);
                adapter.notifyDataSetChanged();
                //  discovery is finished
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {

                Log.d("Discovery","Finished");
                if(devices.size() == 0)
                {
                    Toast.makeText(context, "Busca finalizada", Toast.LENGTH_SHORT).show();

                }
                else
                {
                    Toast.makeText(context, "Não existem bluetooths próximos", Toast.LENGTH_SHORT).show();
                }

            }
        }
    };

}
