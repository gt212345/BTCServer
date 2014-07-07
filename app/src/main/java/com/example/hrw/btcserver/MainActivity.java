package com.example.hrw.btcserver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.UUID;


public class MainActivity extends ActionBarActivity {
    BluetoothAdapter mBluetoothAdapter;
    BluetoothServerSocket mmServerSocket;
    BluetoothSocket mBluetoothSocket;
    ObjectOutputStream mObjectOutputStream;
    ObjectInputStream mObjectInputStream;
    OutputStream mOutputStream;
    InputStream mInputStream;
    Database mDatabase;
    private int GET_HR = 101;
    boolean isBTOpen,isConnected;
    private static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mDatabase = new Database();
        isBTOpen = false;
        isConnected = false;
        final EditText data = (EditText)findViewById(R.id.Data);
        final Button sent = (Button) findViewById(R.id.Sent);
        sent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnected) {
                    try {
                        mOutputStream.write(data.getText().toString().getBytes("UTF-8"));
                        mOutputStream.flush();
                        Log.w("Data","Sent");
                    } catch (IOException e) {
                        Log.w("OutputStream", e.toString());
                    }
                }
            }
        });
        sent.setClickable(false);
        AcceptThread();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(isBTOpen) {
                    try {
                        Log.w("Server", "Waiting to accept");
                        mBluetoothSocket = mmServerSocket.accept();
                        mOutputStream = mBluetoothSocket.getOutputStream();
                        mInputStream = mBluetoothSocket.getInputStream();
                        Log.w("Server", "Waiting to send data");
                        sent.setClickable(true);
                        isConnected = true;
                        new Thread(listenForRequest).start();
                    }catch(IOException e) {
                        Log.w("OutputStream",e.toString());
                    }
                }
            }
        }).start();
    }

    Runnable listenForRequest = new Runnable() {
        @Override
        public void run() {
            while(true) {
                try {
                    if (mInputStream.available() > 0) {
                        int[] temp = isGETHR(mInputStream);
                        if (temp [0] == 1) {
                            ObjectOutputStream ob = new ObjectOutputStream(mOutputStream);
                            ob.writeObject(mDatabase.getData(temp[1]));
                            ob.flush();
                            Log.w("Server: ","Data sent");
                        }
                    }
                }catch (IOException e){
                    Log.w("request",e.toString());
                }catch (ClassNotFoundException e){

                }
            }
        }
    };

    public int[] isGETHR(InputStream inputStream) throws IOException,ClassNotFoundException{
        mObjectInputStream = new ObjectInputStream(inputStream);
        int[] temp = (int [])mObjectInputStream.readObject();
        if (temp[0] == GET_HR) {
            Log.w("Server: ","Request received");
            return new int[]{1,temp[1]};
        }else {
            return new int[]{0,0};
        }
    }

    public void AcceptThread() {
        try {
            mmServerSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("BTC", uuid);
            isBTOpen = true;
        } catch (IOException e) {
            Log.w("server socket",e.toString());
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
