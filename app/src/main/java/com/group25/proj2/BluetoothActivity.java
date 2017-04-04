package com.group25.proj2;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

public class BluetoothActivity extends AppCompatActivity {
    private static final String TAG = "BluetoothConnectionServ";
    private static final String appName = "MYAPP";
    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    public static BluetoothSocket mmSocket = null;
    // input/output “streams” with which we can read and write to device
    // use of “static” important, it means variables can be accessed
    // without an object, this is useful as other activities can use
    // these streams to communicate after they have been opened.
    public static InputStream mmInStream = null;
    public static OutputStream mmOutStream = null;
    public static  BluetoothAdapter mBluetoothAdapter;
    private BroadcastReceiver mReceiver;
    private final static int REQUEST_ENABLE_BT = 1;

    // indicates if we are connected to a device
    private static boolean Connected = false;
    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        context = getApplicationContext();
        // This call returns a handle to the one bluetooth device within your Android device
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // check to see if your android device even has a bluetooth device !!!!,
        if (mBluetoothAdapter == null) {
            Toast toast = Toast.makeText(context, "No Bluetooth!!", Toast.LENGTH_LONG);
            toast.show();
            finish(); // if no bluetooth device on this tablet don’t go any further.
            return;
        }

        // If the bluetooth device is not enabled, let’s turn it on
        if (!mBluetoothAdapter.isEnabled()) {
            // create a new intent that will ask the bluetooth adaptor to “enable” itself.
            // A dialog box will appear asking if you want turn on the bluetooth device
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            // REQUEST_ENABLE_BT below is a constant (defined as '1 - but could be anything)
            // When the “activity” is run and finishes, Android will run your onActivityResult()
            // function (see next page) where you can determine if it was successful or not
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        Set<BluetoothDevice> thePairedDevices = mBluetoothAdapter.getBondedDevices();
        if (thePairedDevices.size() > 0) {
            Iterator<BluetoothDevice> iter = thePairedDevices.iterator();
            BluetoothDevice aNewdevice;
            while (iter.hasNext()) { // while at least one more device
                aNewdevice = iter.next();
                CreateSerialBluetoothDeviceSocket(aNewdevice);
                ConnectToSerialBlueToothDevice();
                System.out.println("End");
            }
        }
        Intent intent = new Intent(BluetoothActivity.this, MenuActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver); // make sure we unregister
    }

    @Override
    public void onBackPressed() {
    }

    public static void CreateSerialBluetoothDeviceSocket(BluetoothDevice device) {
        mmSocket = null;

        // universal UUID for a serial profile RFCOMM blue tooth device
        // this is just one of those “things” that you have to do and just works
        UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        // Get a bluetooth_colour Socket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            mmSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            Toast.makeText(context, "Socket Creation Failed", Toast.LENGTH_SHORT).show();
        }
    }


    public static void ConnectToSerialBlueToothDevice() {
        // Cancel discovery because it will slow down the connection
        mBluetoothAdapter.cancelDiscovery();
        try {
            // Attempt connection to the device through the socket.
            mmSocket.connect();
            Toast.makeText(context, "Connection Made", Toast.LENGTH_LONG).show();
        } catch (IOException connectException) {
            Toast.makeText(context, "Connection Failed", Toast.LENGTH_LONG).show();
            return;
        }

        //create the input/output stream and record fact we have made a connection
        GetInputOutputStreamsForSocket(); // see page 26
        Connected = true;
    }

    // gets the input/output stream associated with the current socket
    public static void GetInputOutputStreamsForSocket() {
        try {
            mmInStream = mmSocket.getInputStream();
            mmOutStream = mmSocket.getOutputStream();
            Toast.makeText(context, "created sockets", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
        }
    }

    public static void WriteToBTDevice(String message) {
        String s = new String("\r\n");
        byte[] msgBuffer = message.getBytes();
        byte[] newline = s.getBytes();

        try {
            mmOutStream.write(msgBuffer);
            mmOutStream.write(newline);
        } catch (IOException e) {
        }
    }

    public static String ReadFromBTDevice() {
        byte c;
        String s = new String("");

        try { // Read from the InputStream using polling and timeout
            //while (true) { // try to read for 2 seconds max
            for (int i = 0; i < 50; i++) {
                SystemClock.sleep(10);
                if (mmInStream.available() > 0) {
                    //if ((c = (byte) mmInStream.read()) == '$') // '$' terminator
                    //break;
                    //else
                    c = (byte) mmInStream.read();
                    s += (char) c; // build up string 1 byte by byte}
                }
            }
        } catch (IOException e) {
            //return new String("-- No Response --");
            return "";
        }

        //int length = s.length();
        //return s.substring(length - 1);

        return s;
    }

    public static void sendToDE2(String s){
        WriteToBTDevice(s);
        System.out.println(s);
    }

    public static String readFromDE2(){
        String s = "";
        s = ReadFromBTDevice();
        return s;
    }
}
