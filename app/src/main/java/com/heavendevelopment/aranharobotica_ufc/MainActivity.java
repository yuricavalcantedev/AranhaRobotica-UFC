package com.heavendevelopment.aranharobotica_ufc;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public boolean send;
    boolean checkStatus = false;

    private BluetoothDevice deviceBluetooth; // dispositvo remoto blutooth
    private BluetoothAdapter myBluetoothAdapter = null; // Adapter local do bluetooth
    private BluetoothSocket socketBluetooth = null; // Socket bluetooth para conexão

    OutputStream myOutputStream;
    InputStream myInputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;
    protected PowerManager.WakeLock mWakeLock;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        //this.mWakeLock.acquire();

        //Recebe o objeto selecionado na classe DispositivosPareadosActivity
        Device dev = (Device) getIntent().getSerializableExtra("device");
        if (dev != null) {
            selectDevice(dev);
            try {
                boolean conectado = openConnection();
                if (conectado) {
                    send = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Button btMoveRight = (Button) findViewById(R.id.bt_mover_direita);
        btMoveRight.setOnClickListener(this);
    }

    public void selectDevice(Device d) {
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = myBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getAddress().equals(d.getMacAddress())){
                    deviceBluetooth = device;
                    break;
                }
            }
        }
    }

    /**
     * Metodo que abre a conexao com o dispositivo serial
     **/
    public boolean openConnection() throws IOException {
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");//UUID padrao para dispositivos seriais

        socketBluetooth = deviceBluetooth.createRfcommSocketToServiceRecord(uuid);

        myBluetoothAdapter.cancelDiscovery();
        try {
            socketBluetooth.connect();
            checkStatus = true;
            Toast.makeText(this, "Conectado", Toast.LENGTH_SHORT).show();
        } catch (IOException e2) {
            Toast.makeText(this, "Dispositivo Serial não encontrado",Toast.LENGTH_SHORT).show();
            checkStatus = false;
            try {
                socketBluetooth.close();
            } catch (IOException closeException) {
                closeException.printStackTrace();
            }
            return checkStatus;
        }

        Toast.makeText(this, "Connected",Toast.LENGTH_SHORT).show();

        myOutputStream = socketBluetooth.getOutputStream();
        myInputStream = socketBluetooth.getInputStream();

        beginListenForData();
        return checkStatus;
    }

    void sendData(int x) throws IOException {
        myOutputStream.write((byte) 33);
        myOutputStream.write((byte)x);

    }

    void closeBT() throws IOException {
        stopWorker = true;
        myOutputStream.close();
        myInputStream.close();
        socketBluetooth.close();
        checkStatus = false;

    }

    public void onDestroy() {
        this.mWakeLock.release();
        super.onDestroy();

        if (socketBluetooth != null) {
            try {
                closeBT();
            } catch (IOException e) {
            }
        }
    }

    void beginListenForData() {
        final Handler handler = new Handler();
        final byte delimiter = 10; // This is the ASCII code for a newline
        // character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable() {
            public void run() {
                while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                    try {
                        int bytesAvailable = myInputStream.available();
                        if (bytesAvailable > 0) {
                            byte[] packetBytes = new byte[bytesAvailable];
                            myInputStream.read(packetBytes);
                            for (int i = 0; i < bytesAvailable; i++) {
                                byte b = packetBytes[i];
                                if (b == delimiter) {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0,
                                            encodedBytes, 0,
                                            encodedBytes.length);
                                    final String data = new String(
                                            encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;

                                    handler.post(new Runnable() {
                                        public void run() {
                                        }
                                    });
                                } else {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    } catch (IOException ex) {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }

    @Override
    public void onClick(View v) {

                Toast.makeText(MainActivity.this, "Mova para a direita", Toast.LENGTH_SHORT).show();

                int idBotao = v.getId();

                try {

                    switch (idBotao) {

                        case R.id.bt_mover_frente: sendData((byte) 'w');
                            break;
                        case R.id.bt_mover_tras: sendData((byte) 's');
                            break;
                        case R.id.bt_mover_esquerda: sendData((byte) 'a');
                            break;
                        case R.id.bt_mover_direita: sendData((byte) 'd');
                            break;
                        case R.id.bt_posicao_inicial: sendData((byte) 'r');
                            break;
                        case R.id.bt_mover_tchau: sendData((byte) 'b');
                            break;
                    }

                } catch (IOException e) {
                    Toast.makeText(this, "Ocorreu um erro ao enviar o comando. Tente novamente.", Toast.LENGTH_SHORT).show();
                }
    }
}