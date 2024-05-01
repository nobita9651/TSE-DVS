package com.example.pea;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    private static final int REQUEST_ENABLE_BT = 5;
    private String deviceName = null;
    private String deviceAddress;
    public static Handler handler;
    public static BluetoothSocket mmSocket;
    public static ConnectedThread connectedThread;
    public static CreateConnectThread createConnectThread;
    private String RegNo;

    int fineCount;
    ProgressBar progressBar;
    TextView connectedDevice,regNumText;
    Button buttonVerify;
    Button bluetoothDevCon;

    BluetoothManager bluetoothManager;
    BluetoothAdapter bluetoothAdapter;

    String regCertExpiryDate;
    String polCertExpiryDate;
    String seatBeltStatus,seatBeltStatusComplete;
    TextView regCertExpiryDateText, polCertExpiryDateText, seatBeltStatusText;

    MaterialCardView regCertBox, polCertBox, seatBeltBox;

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final static int CONNECTING_STATUS = 1; // used in bluetooth handler to identify message status
    private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetoothDevCon = findViewById(R.id.btDeviceConnect);
        bluetoothManager = getSystemService(BluetoothManager.class);
        bluetoothAdapter = bluetoothManager.getAdapter();
        progressBar = findViewById(R.id.verifyProgress);
        connectedDevice = findViewById(R.id.btDeviceNameText);
        regNumText = findViewById(R.id.regNumText);
        buttonVerify = findViewById(R.id.verifyButton);

        progressBar.setVisibility(View.GONE);

        regCertExpiryDateText = findViewById(R.id.rcValidDate);
        polCertExpiryDateText = findViewById(R.id.pucValidDate);
        seatBeltStatusText = findViewById(R.id.seatBeltStatus);

        regCertBox = findViewById(R.id.regCertCardView);
        polCertBox = findViewById(R.id.polCertCardView);
        seatBeltBox = findViewById(R.id.seatBeltCardView);

        if (bluetoothAdapter == null) {
            Log.d(TAG, "onCreate: Device doesn't support Bluetooth");
        }

        bluetoothDevCon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SelectDeviceActivity.class);
                startActivity(intent);
            }
        });

        buttonVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String dateToday = dateFormat.format(new Date());
//                RegNo = "KA04AB1234";
                fineCount = 0;
                progressBar.setVisibility(View.VISIBLE);
                connectedThread.write("1");
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        FirebaseDatabase.getInstance().getReference("RegistrationCertificate")
                                .child(RegNo).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        RegCertInfo regCertInfo = snapshot.getValue(RegCertInfo.class);
                                        regCertExpiryDate = regCertInfo.rcExpiryDate;
                                        if(regCertInfo != null){
                                            try {
                                                Date presDate = dateFormat.parse(dateToday);
                                                Date rcDate = dateFormat.parse(regCertInfo.rcExpiryDate);
                                                if(rcDate.before(presDate)){
//                                                    regCertBox.setStrokeColor(Color.parseColor("#FF0000"));
                                                    fineCount++;
                                                    regCertExpiryDateText.setTextColor(Color.parseColor("#FF0000"));
                                                    regCertExpiryDateText.setText("Valid Upto : " + regCertExpiryDate);
                                                }
                                                else{
//                                                    regCertBox.setStrokeColor(Color.parseColor("#0B6623"));
                                                    regCertExpiryDateText.setTextColor(Color.parseColor("#0B6623"));
                                                    regCertExpiryDateText.setText("Valid Upto : " + regCertExpiryDate);
                                                }
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(MainActivity.this, "Error retrieving data!", Toast.LENGTH_SHORT).show();
                                    }
                                });

                        FirebaseDatabase.getInstance().getReference("PollutionCertificate")
                                .child(RegNo).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        PoluCertInfo poluCertInfo = snapshot.getValue(PoluCertInfo.class);
                                        polCertExpiryDate = poluCertInfo.polCertExpiry;
                                        if(poluCertInfo != null){
                                            try {
                                                Date presDate = dateFormat.parse(dateToday);
                                                Date rcDate = dateFormat.parse(poluCertInfo.polCertExpiry);
                                                if(rcDate.before(presDate)){
//                                                    polCertBox.setStrokeColor(Color.parseColor("#FF0000"));
                                                    fineCount++;
                                                    polCertExpiryDateText.setTextColor(Color.parseColor("#FF0000"));
                                                    polCertExpiryDateText.setText("Valid Upto : " + polCertExpiryDate);
                                                }
                                                else{
//                                                    polCertBox.setStrokeColor(Color.parseColor("#0B6623"));
                                                    polCertExpiryDateText.setTextColor(Color.parseColor("#0B6623"));
                                                    polCertExpiryDateText.setText("Valid Upto : " + polCertExpiryDate);
                                                }
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(MainActivity.this, "Error retrieving data!", Toast.LENGTH_SHORT).show();
                                    }
                                });

                        FirebaseDatabase.getInstance().getReference("Seatbelt")
                                .child(RegNo).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        SeatBeltInfo seatBeltInfo = snapshot.getValue(SeatBeltInfo.class);
                                        seatBeltStatus = seatBeltInfo.sb_st;
                                        if(seatBeltInfo != null){
                                            if(seatBeltStatus.equals("0")){
//                                                seatBeltBox.setStrokeColor(Color.parseColor("#FF0000"));
                                                fineCount++;
                                                seatBeltStatusText.setTextColor(Color.parseColor("#FF0000"));
                                                seatBeltStatusComplete = "Not Worn!";
                                            }
                                            else if(seatBeltStatus.equals("1")){
//                                                seatBeltBox.setStrokeColor(Color.parseColor("#0B6623"));
                                                seatBeltStatusText.setTextColor(Color.parseColor("#0B6623"));
                                                seatBeltStatusComplete = "Worn!";
                                            }
                                            else if(seatBeltStatus.equals("2")){
//                                                seatBeltBox.setStrokeColor(Color.parseColor("#000000"));
                                                seatBeltStatusText.setTextColor(Color.parseColor("#000000"));
                                                seatBeltStatusComplete = "NA!";
                                            }
                                        }
                                        seatBeltStatusText.setText("Status : " + seatBeltStatusComplete);

                                        FineCount fineCountObj = new FineCount(fineCount);
                                        FirebaseDatabase.getInstance().getReference("FineCount")
                                                .child(RegNo).setValue(fineCountObj).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            Toast.makeText(MainActivity.this, "Done", Toast.LENGTH_SHORT).show();
                                                        }
                                                        else{
                                                            Toast.makeText(MainActivity.this, "Upload Failed! Try Again", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });

                                        progressBar.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(MainActivity.this, "Error retrieving data!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }, 5000);
            }
        });

        // If a bluetooth device has been selected from SelectDeviceActivity
        deviceName = getIntent().getStringExtra("deviceName");
        if (deviceName != null) {
            // Get the device address to make BT Connection
            connectedDevice.setText("Connected to : " + deviceName);
            deviceAddress = getIntent().getStringExtra("deviceAddress");
            // Show progress and connection status
//            toolbar.setSubtitle("Connecting to " + deviceName + "...");
            progressBar.setVisibility(View.VISIBLE);
            bluetoothDevCon.setEnabled(false);

            /*
            This is the most important piece of code. When "deviceName" is found
            the code will call a new thread to create a bluetooth connection to the
            selected device (see the thread code below)
             */

            createConnectThread = new CreateConnectThread(bluetoothAdapter, deviceAddress);
            createConnectThread.start();
        }

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case CONNECTING_STATUS:
                        switch (msg.arg1) {
                            case 1:
//                                toolbar.setSubtitle("Connected to " + deviceName);
                                Toast.makeText(MainActivity.this, "Connected to " + deviceName, Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                bluetoothDevCon.setEnabled(true);
                                break;
                            case -1:
//                                toolbar.setSubtitle("Device fails to connect");
                                Toast.makeText(MainActivity.this, "Device fails to connect", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                bluetoothDevCon.setEnabled(true);
                                break;
                        }
                        break;

                    case MESSAGE_READ:
                        String deviceMsg = msg.obj.toString();
                        Log.d(TAG, "handleMessage: " + deviceMsg);
                        RegNo = deviceMsg;
                        regNumText.setText("Reg No : " + deviceMsg);
                        break;
                }
            }
        };
    }

    /* ============================ Thread to Create Bluetooth Connection =================================== */
    public class CreateConnectThread extends Thread {

        public CreateConnectThread(BluetoothAdapter bluetoothAdapter, String address) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);
            BluetoothSocket tmp = null;
            UUID uuid = null;
            if(ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {}
//            uuid = bluetoothDevice.getUuids()[0].getUuid();
            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
            /*
            Due to Android device varieties,the method below may not work fo different devices.
            You should try using other methods i.e. :
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
             */
                tmp = bluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID);
//                tmp = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(uuid);

            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {}
            bluetoothAdapter.cancelDiscovery();
            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
                Log.e("Status", "Device connected");
                handler.obtainMessage(CONNECTING_STATUS, 1, -1).sendToTarget();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                    Log.e("Status", "Cannot connect to device");
                    handler.obtainMessage(CONNECTING_STATUS, -1, -1).sendToTarget();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
                return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            connectedThread = new ConnectedThread(mmSocket);
            connectedThread.run();
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }
    }

    /* =============================== Thread for Data Transfer =========================================== */
    public static class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private  boolean createString = false;
        private String readMessage = "-";


        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes = 0; // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    buffer[bytes] = (byte) mmInStream.read();
                    String readByte = new String(buffer, StandardCharsets.UTF_8).substring(0,1);

                    //
                    switch (readByte){
                        case "<":
                            createString = true;
                            readMessage = "";
                            break;
                        case ">":
                            createString = false;
                            break;
                    }

                    //
                    if (createString){
                        readMessage = readMessage + readByte;
                    } else {
//                            Log.e("UID Length", readMessage.length() + " characters");
                            String readUID = readMessage.substring(1).trim();
                            handler.obtainMessage(MESSAGE_READ, readUID).sendToTarget();
                        }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

        public void write(String input) {
            byte[] bytes = input.getBytes(); //converts entered String into bytes
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Log.e("Send Error","Unable to send message",e);
            }
        }
    }
}