package com.heavendevelopment.aranharobotica_ufc;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.UUID;

public class HomeActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private Button btEstadoBluetoothMain;
    private TextView tvEstadoBluetoothMain;
    private TextView tvMensagemBluetoothMain;
    private  boolean bluetoothLigado = false;

    private BluetoothDevice dispositivoBluetoohRemoto; // dispositvo remoto blutooth
    private BluetoothAdapter meuBluetoothAdapter = null; // Adapter local do bluetooth
    private BluetoothSocket bluetoothSocket = null; // Socket bluetooth para conexão
    private static final String endereco_MAC_do_Bluetooth_Remoto = "20:14:05:15:32:00"; // representa o endereço remoto do bluetooth
    public static final int CODIGO_LIGAR_BLUETOOTH = 1;

    //ainda não vou usar


    // Anyone can create a UUID and use it to identify something with
    // reasonable confidence that the same identifier will never be
    // unintentionally created by anyone to identify something else
    private static final UUID MEU_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // A readable source of bytes.
    private InputStream inputStream = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Aranha Robótica -UFC");
        toolbar.setSubtitle("Home");

        setSupportActionBar(toolbar);

        tvEstadoBluetoothMain = (TextView) findViewById(R.id.tv_estado_main);
        tvMensagemBluetoothMain = (TextView) findViewById(R.id.tv_mensagem_main);

        btEstadoBluetoothMain = (Button) findViewById(R.id.bt_estado_bluetooth_main);
        btEstadoBluetoothMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!bluetoothLigado)
                    ligarBluetooth();
                else
                    conectarAranha();

            }
        });

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void ligarBluetooth(){

        //pega o adapter default do bluetooth
        meuBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Verifica se o celular tem Bluetooth
        if(meuBluetoothAdapter == null){

            Toast.makeText(getApplicationContext(), "Dispositivo não possui hardware Bluetooth", Toast.LENGTH_LONG).show();

        } else {

            // Verifica se o bluetooth está desligado. Se sim, pede permissão para ligar.
            if(!meuBluetoothAdapter.isEnabled()){

                Intent novoIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(novoIntent, CODIGO_LIGAR_BLUETOOTH);

            }
        }
    }

    private void conectarAranha(){

        //conectar ao bluettoth da aranha
        //colocar uma mensagem de confirmação que conectou na aranha.
        //...

        //depois de conectar ...

        fab.setVisibility(View.VISIBLE);
        btEstadoBluetoothMain.setEnabled(false);
        btEstadoBluetoothMain.setVisibility(View.INVISIBLE);

    }

    //calback do alert para ligar o bluetooth
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){

            case CODIGO_LIGAR_BLUETOOTH:

                if(resultCode == Activity.RESULT_OK){

                    Toast.makeText(getApplicationContext(), "Bluetooth foi ativado", Toast.LENGTH_LONG).show();

                    btEstadoBluetoothMain.setText(getString(R.string.bt_estado_bluetooth_on));
                    tvEstadoBluetoothMain.setText(getString(R.string.estado_bluetooth_on));
                    tvMensagemBluetoothMain.setText(getString(R.string.mensagem_bluetooth_on));

                    bluetoothLigado = true;

                } else {
                    Toast.makeText(getApplicationContext(), "Bluetooth não foi ativado", Toast.LENGTH_LONG).show();
                }

                break;
        }
    }

}
