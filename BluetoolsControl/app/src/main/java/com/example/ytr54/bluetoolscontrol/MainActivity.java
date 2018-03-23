package com.example.ytr54.bluetoolscontrol;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback{

    //蓝牙适配器
    private BluetoothAdapter mBluetoothAdapter;
    //搜索BUTTON
    private Button scan;
    //搜索结果List
    private ListView resultList;
    //搜索状态的标示
    private boolean mScanning;
    //扫描时长
    private static final long SCAN_PERIOD = 5000;
    //请求启用蓝牙请求码
    private static final int REQUEST_ENABLE_BT = 1234;
    //蓝牙适配器
    private BlueToothDeviceAdapter mBlueToothDeviceAdapter;
    //蓝牙适配器List
    private List<BluetoothDevice> mBlueList = new ArrayList<>();
    private Context context;
    private int REQUEST_ACCESS_COARSE_LOCATION=1;
    //send
    private static final boolean D = true;
    private BluetoothSocket btSocket = null;  //手机蓝牙与蓝牙模块之间的socket
    private OutputStream outStream = null;  //发送指令的输出流
    Button mButtonF;

    Button mButtonB;
    Button mButtonL;
    Button mButtonR;
    Button mButtonS;
    Button mButtonConnect;

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    //要连接的蓝牙设备MAC地址
    String address;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(Build.VERSION.SDK_INT>=23){
            //判断是否有权限
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
                //请求权限
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_ACCESS_COARSE_LOCATION);
            //向用户解释，为什么要申请该权限
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_CONTACTS)) {
                    Toast.makeText(MainActivity.this,"shouldShowRequestPermissionRationale", Toast.LENGTH_SHORT).show();
                }
            }
        }

        context = this;
        //初始化蓝牙适配器
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        //初始化适配器
        mBlueToothDeviceAdapter = new BlueToothDeviceAdapter(mBlueList,context);

        scan = (Button) findViewById(R.id.scan);
        resultList = (ListView) findViewById(R.id.result);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inint();
            }
        });
        resultList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                BluetoothDevice device=(BluetoothDevice)resultList.getItemAtPosition(i);
                address=device.getAddress();
                Log.e("tag", "点击结果   " + address);

            }
        });
       // Button mButtonConnect=(Button)findViewById(R.id.connect);
        /*mButtonConnect.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View view) {

            }
        });*/
        mButtonF=(Button)findViewById(R.id.btnF);
        mButtonF.setOnTouchListener(new Button.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
// TODO Auto-generated method stub
                String message;
                byte[] msgBuffer;
                int action = event.getAction();
                switch(action)
                {
                    case MotionEvent.ACTION_DOWN:  //按下了前进按钮
                        try {
                            outStream = btSocket.getOutputStream();  //通过socket 得到输出流

                        } catch (IOException e) {
                            Log.e("tag", "ON RESUME: Output stream creation failed.", e);
                        }


                        message = "$1,0,0,0,0,0,0,0,0,0,0,0,4200#";  //上位机与下位机约定的指令

                        msgBuffer = message.getBytes();  //因为outputStream 只能传输字节，所以要把字符串指令编程字节流

                        try {
                            outStream.write(msgBuffer);  //将指令写入输出流中。也就是写入socket中

                        } catch (IOException e) {
                            Log.e("tag", "ON RESUME: Exception during write.", e);
                        }
                        break;

                    case MotionEvent.ACTION_UP:  //松开了前进按钮，与前面类似，只是指令不同。
                        try {
                            outStream = btSocket.getOutputStream();

                        } catch (IOException e) {
                            Log.e("tag", "ON RESUME: Output stream creation failed.", e);
                        }


                        message = "$0,0,0,0,0,0,0,0,0,0,0,0,4200#";

                        msgBuffer = message.getBytes();

                        try {
                            outStream.write(msgBuffer);

                        } catch (IOException e) {
                            Log.e("tag", "ON RESUME: Exception during write.", e);
                        }
                        break;
                }
                return false;
            }


        });

        mButtonB=(Button)findViewById(R.id.btnB);
        mButtonB.setOnTouchListener(new Button.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
// TODO Auto-generated method stub
                String message;
                byte[] msgBuffer;
                int action = event.getAction();
                switch(action)
                {
                    case MotionEvent.ACTION_DOWN:  //按下了前进按钮
                        try {
                            outStream = btSocket.getOutputStream();  //通过socket 得到输出流

                        } catch (IOException e) {
                            Log.e("tag", "ON RESUME: Output stream creation failed.", e);
                        }


                        message = "$2,0,0,0,0,0,0,0,0,0,0,0,4200#";  //上位机与下位机约定的指令

                        msgBuffer = message.getBytes();  //因为outputStream 只能传输字节，所以要把字符串指令编程字节流

                        try {
                            outStream.write(msgBuffer);  //将指令写入输出流中。也就是写入socket中

                        } catch (IOException e) {
                            Log.e("tag", "ON RESUME: Exception during write.", e);
                        }
                        break;

                    case MotionEvent.ACTION_UP:  //松开了前进按钮，与前面类似，只是指令不同。
                        try {
                            outStream = btSocket.getOutputStream();

                        } catch (IOException e) {
                            Log.e("tag", "ON RESUME: Output stream creation failed.", e);
                        }


                        message = "$0,0,0,0,0,0,0,0,0,0,0,0,4200#";

                        msgBuffer = message.getBytes();

                        try {
                            outStream.write(msgBuffer);

                        } catch (IOException e) {
                            Log.e("tag", "ON RESUME: Exception during write.", e);
                        }
                        break;
                }
                return false;
            }


        });

        mButtonL=(Button)findViewById(R.id.btnL);
        mButtonL.setOnTouchListener(new Button.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
// TODO Auto-generated method stub
                String message;
                byte[] msgBuffer;
                int action = event.getAction();
                switch(action)
                {
                    case MotionEvent.ACTION_DOWN:  //按下了前进按钮
                        try {
                            outStream = btSocket.getOutputStream();  //通过socket 得到输出流

                        } catch (IOException e) {
                            Log.e("tag", "ON RESUME: Output stream creation failed.", e);
                        }


                        message = "$3,0,0,0,0,0,0,0,0,0,0,0,4200#";  //上位机与下位机约定的指令

                        msgBuffer = message.getBytes();  //因为outputStream 只能传输字节，所以要把字符串指令编程字节流

                        try {
                            outStream.write(msgBuffer);  //将指令写入输出流中。也就是写入socket中

                        } catch (IOException e) {
                            Log.e("tag", "ON RESUME: Exception during write.", e);
                        }
                        break;

                    case MotionEvent.ACTION_UP:  //松开了前进按钮，与前面类似，只是指令不同。
                        try {
                            outStream = btSocket.getOutputStream();

                        } catch (IOException e) {
                            Log.e("tag", "ON RESUME: Output stream creation failed.", e);
                        }


                        message = "$0,0,0,0,0,0,0,0,0,0,0,0,4200#";

                        msgBuffer = message.getBytes();

                        try {
                            outStream.write(msgBuffer);

                        } catch (IOException e) {
                            Log.e("tag", "ON RESUME: Exception during write.", e);
                        }
                        break;
                }
                return false;
            }


        });

        mButtonR=(Button)findViewById(R.id.btnR);
        mButtonR.setOnTouchListener(new Button.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
// TODO Auto-generated method stub
                String message;
                byte[] msgBuffer;
                int action = event.getAction();
                switch(action)
                {
                    case MotionEvent.ACTION_DOWN:  //按下了前进按钮
                        try {
                            outStream = btSocket.getOutputStream();  //通过socket 得到输出流

                        } catch (IOException e) {
                            Log.e("tag", "ON RESUME: Output stream creation failed.", e);
                        }


                        message = "$4,0,0,0,0,0,0,0,0,0,0,0,4200#";  //上位机与下位机约定的指令

                        msgBuffer = message.getBytes();  //因为outputStream 只能传输字节，所以要把字符串指令编程字节流

                        try {
                            outStream.write(msgBuffer);  //将指令写入输出流中。也就是写入socket中

                        } catch (IOException e) {
                            Log.e("tag", "ON RESUME: Exception during write.", e);
                        }
                        break;

                    case MotionEvent.ACTION_UP:  //松开了前进按钮，与前面类似，只是指令不同。
                        try {
                            outStream = btSocket.getOutputStream();

                        } catch (IOException e) {
                            Log.e("tag", "ON RESUME: Output stream creation failed.", e);
                        }


                        message = "$0,0,0,0,0,0,0,0,0,0,0,0,4200#";

                        msgBuffer = message.getBytes();

                        try {
                            outStream.write(msgBuffer);

                        } catch (IOException e) {
                            Log.e("tag", "ON RESUME: Exception during write.", e);
                        }
                        break;
                }
                return false;
            }


        });

        mButtonS=(Button)findViewById(R.id.btnS);
        mButtonS.setOnTouchListener(new Button.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
// TODO Auto-generated method stub
                String message;
                byte[] msgBuffer;
                int action = event.getAction();
                switch(action)
                {
                    case MotionEvent.ACTION_DOWN:  //按下了前进按钮
                        try {
                            outStream = btSocket.getOutputStream();  //通过socket 得到输出流

                        } catch (IOException e) {
                            Log.e("tag", "ON RESUME: Output stream creation failed.", e);
                        }


                        message = "$0,0,0,0,0,0,0,0,0,0,0,0,4200#";  //上位机与下位机约定的指令

                        msgBuffer = message.getBytes();  //因为outputStream 只能传输字节，所以要把字符串指令编程字节流

                        try {
                            outStream.write(msgBuffer);  //将指令写入输出流中。也就是写入socket中

                        } catch (IOException e) {
                            Log.e("tag", "ON RESUME: Exception during write.", e);
                        }
                        break;

                    case MotionEvent.ACTION_UP:  //松开了前进按钮，与前面类似，只是指令不同。
                        try {
                            outStream = btSocket.getOutputStream();

                        } catch (IOException e) {
                            Log.e("tag", "ON RESUME: Output stream creation failed.", e);
                        }


                        message = "$0,0,0,0,0,0,0,0,0,0,0,0,4200#";

                        msgBuffer = message.getBytes();

                        try {
                            outStream.write(msgBuffer);

                        } catch (IOException e) {
                            Log.e("tag", "ON RESUME: Exception during write.", e);
                        }
                        break;
                }
                return false;
            }


        });

        mButtonConnect=(Button)findViewById(R.id.connect);
        mButtonConnect.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                try {

                    btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);

                } catch (IOException e) {

                    Log.e("tag", "ON RESUME: Socket creation failed.", e);

                }
                mBluetoothAdapter.cancelDiscovery();
                try {

                    btSocket.connect();

                    Log.e("tag", "ON RESUME: BT connection established, data transfer link open.");

                } catch (IOException e) {

                    try {
                        btSocket.close();

                    } catch (IOException e2) {

                        Log .e("tag","ON RESUME: Unable to close socket during connection failure", e2);
                    }

                }

            }
        });





    }


    private void inint() {
        scanLeDevice(true);
    }

    /**
     * 设备搜索
     *
     * @param enable 是否正在搜索的标示
     */
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mScanning) {
                        mScanning = false;
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    }
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mHandler.sendEmptyMessage(1);
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        }
    }

    // Hander
    public final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: // Notify change
                    mBlueToothDeviceAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };
    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //获取到蓝牙设备
                            if (!mBlueList.contains(device)){
                                mBlueList.add(device);
                                Log.e("tag", "mLeScanCallback 搜索结果   " + device.getAddress());
                            }
                            //List加载适配器
                            if (mBlueToothDeviceAdapter.isEmpty()) {
                                Log.e("tag", "mLeDeviceListAdapter为空");

                            } else {
                                resultList.setAdapter(mBlueToothDeviceAdapter);

                            }
                            mHandler.sendEmptyMessage(1);
                        }
                    });
                }
            };


    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
        // TODO Auto-generated method stub
        if (requestCode == REQUEST_ACCESS_COARSE_LOCATION) {
            if (permissions[0] .equals(Manifest.permission.ACCESS_COARSE_LOCATION)
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 用户同意使用该权限
            } else {
                // 用户不同意，向用户展示该权限作用
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    //showTipDialog("用来扫描附件蓝牙设备的权限，请手动开启！");
                    return;
                }
            }
        }
    }

}