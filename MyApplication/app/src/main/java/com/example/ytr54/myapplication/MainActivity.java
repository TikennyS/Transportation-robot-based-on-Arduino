package com.example.ytr54.myapplication;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "tag";

    private static final boolean D = true;

    private BluetoothAdapter mBluetoothAdapter = null;

    private BluetoothSocket btSocket = null;  //手机蓝牙与蓝牙模块之间的socket

    private OutputStream outStream = null;  //发送指令的输出流
    private InputStream inputStream= null;  //发送指令的输出流
    Button mButtonF;

    Button mButtonB;
    Button mButtonL;
    Button mButtonR;
    Button mButtonS;

    Button mButtonNormal;
    Button mButtonTrack;
    Button mButtonFollow;

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    private static String address = "98:D3:31:FC:A6:B9"; // <==要连接的蓝牙设备MAC地址
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //解析



        //前进
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
                            Log.e(TAG, "ON RESUME: Output stream creation failed.", e);
                        }


                        message = "$1,0,0,0,0,0,0,0,0,0,0,0,4200#";  //上位机与下位机约定的指令1表示前进

                        msgBuffer = message.getBytes();  //因为outputStream 只能传输字节，所以要把字符串指令编程字节流

                        try {
                            outStream.write(msgBuffer);  //将指令写入输出流中。也就是写入socket中

                        } catch (IOException e) {
                            Log.e(TAG, "ON RESUME: Exception during write.", e);
                        }
                        byte[] msgBufferReceive = new byte[50];
                        int bytes = 0;
                        String receivemessage;
                        try {
                            inputStream=btSocket.getInputStream();
                            bytes=inputStream.read(msgBufferReceive);
                            receivemessage=new String(msgBufferReceive,0,bytes,"UTF8");
                            if(receivemessage.indexOf("c")>=2)
                            {
                                receivemessage=receivemessage.substring(receivemessage.indexOf("c")-2,receivemessage.indexOf("c"));
                                Log.e("接收的数据",receivemessage);
                            }
                        } catch (IOException e) {
                            Log.e(TAG, "ON RESUME: Input stream Read failed.", e);
                        }
                        break;

                    case MotionEvent.ACTION_UP:  //松开了前进按钮，与前面类似，只是指令不同。
                        try {
                            outStream = btSocket.getOutputStream();

                        } catch (IOException e) {
                            Log.e(TAG, "ON RESUME: Output stream creation failed.", e);
                        }


                        message = "$0,0,0,0,0,0,0,0,0,0,0,0,4200#";

                        msgBuffer = message.getBytes();

                        try {
                            outStream.write(msgBuffer);

                        } catch (IOException e) {
                            Log.e(TAG, "ON RESUME: Exception during write.", e);
                        }
                        msgBufferReceive = new byte[50];
                        try {
                            inputStream=btSocket.getInputStream();
                            bytes=inputStream.read(msgBufferReceive);
                            receivemessage=new String(msgBufferReceive,0,bytes,"UTF8");
                            if(receivemessage.indexOf("c")>=2)
                            {
                                receivemessage=receivemessage.substring(receivemessage.indexOf("c")-2,receivemessage.indexOf("c"));
                                Log.e("接收的数据",receivemessage);
                            }
                        } catch (IOException e) {
                            Log.e(TAG, "ON RESUME: Input stream Read failed.", e);
                        }
                        break;
                }

                return false;
            }


        });
//后退
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
                    case MotionEvent.ACTION_DOWN:
                        try {
                            outStream = btSocket.getOutputStream();

                        } catch (IOException e) {
                            Log.e(TAG, "ON RESUME: Output stream creation failed.", e);
                        }


                        message = "$2,0,0,0,0,0,0,0,0,0,0,0,4200#";

                        msgBuffer = message.getBytes();

                        try {
                            outStream.write(msgBuffer);

                        } catch (IOException e) {
                            Log.e(TAG, "ON RESUME: Exception during write.", e);
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        try {
                            outStream = btSocket.getOutputStream();

                        } catch (IOException e) {
                            Log.e(TAG, "ON RESUME: Output stream creation failed.", e);
                        }


                        message = "$0,0,0,0,0,0,0,0,0,0,0,0,4200#";

                        msgBuffer = message.getBytes();

                        try {
                            outStream.write(msgBuffer);

                        } catch (IOException e) {
                            Log.e(TAG, "ON RESUME: Exception during write.", e);
                        }
                        break;
                }

                return false;
            }


        });
//左转
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
                    case MotionEvent.ACTION_DOWN:
                        try {
                            outStream = btSocket.getOutputStream();

                        } catch (IOException e) {
                            Log.e(TAG, "ON RESUME: Output stream creation failed.", e);
                        }


                        message = "$3,0,0,0,0,0,0,0,0,0,0,0,4200#";

                        msgBuffer = message.getBytes();

                        try {
                            outStream.write(msgBuffer);

                        } catch (IOException e) {
                            Log.e(TAG, "ON RESUME: Exception during write.", e);
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        try {
                            outStream = btSocket.getOutputStream();

                        } catch (IOException e) {
                            Log.e(TAG, "ON RESUME: Output stream creation failed.", e);
                        }


                        message = "$0,0,0,0,0,0,0,0,0,0,0,0,4200#";

                        msgBuffer = message.getBytes();

                        try {
                            outStream.write(msgBuffer);

                        } catch (IOException e) {
                            Log.e(TAG, "ON RESUME: Exception during write.", e);
                        }
                        break;
                }

                return false;

            }
        });
//右转
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
                    case MotionEvent.ACTION_DOWN:
                        try {
                            outStream = btSocket.getOutputStream();

                        } catch (IOException e) {
                            Log.e(TAG, "ON RESUME: Output stream creation failed.", e);
                        }


                        message = "$4,0,0,0,0,0,0,0,0,0,0,0,4200#";

                        msgBuffer = message.getBytes();

                        try {
                            outStream.write(msgBuffer);

                        } catch (IOException e) {
                            Log.e(TAG, "ON RESUME: Exception during write.", e);
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        try {
                            outStream = btSocket.getOutputStream();

                        } catch (IOException e) {
                            Log.e(TAG, "ON RESUME: Output stream creation failed.", e);
                        }


                        message = "$0,0,0,0,0,0,0,0,0,0,0,0,4200#";

                        msgBuffer = message.getBytes();

                        try {
                            outStream.write(msgBuffer);

                        } catch (IOException e) {
                            Log.e(TAG, "ON RESUME: Exception during write.", e);
                        }
                        break;
                }

                return false;

            }


        });

        //停止
        mButtonS=(Button)findViewById(R.id.btnS);
        mButtonS.setOnTouchListener(new Button.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
// TODO Auto-generated method stub
                if(event.getAction()==MotionEvent.ACTION_DOWN)
                    try {
                        outStream = btSocket.getOutputStream();

                    } catch (IOException e) {
                        Log.e(TAG, "ON RESUME: Output stream creation failed.", e);
                    }


                String message = "$0,0,0,0,0,0,0,0,0,0,0,0,4200#";

                byte[] msgBuffer = message.getBytes();

                try {
                    outStream.write(msgBuffer);

                } catch (IOException e) {
                    Log.e(TAG, "ON RESUME: Exception during write.", e);
                }
                return false;
            }


        });
        //正常模式
        mButtonNormal=(Button)findViewById(R.id.btnNormal);
        mButtonNormal.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    outStream = btSocket.getOutputStream();

                } catch (IOException e) {
                    Log.e(TAG, "ON RESUME: Output stream creation failed.", e);
                }
                String message = "$MODE0,0,0,0,0,0,0,0,0,0,4200#";
                byte[] msgBuffer = message.getBytes();
                try {
                    outStream.write(msgBuffer);

                } catch (IOException e) {
                    Log.e(TAG, "ON RESUME: Exception during write.", e);
                }
            }
        });

        mButtonTrack=(Button)findViewById(R.id.btnTrack);
        mButtonTrack.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    outStream = btSocket.getOutputStream();

                } catch (IOException e) {
                    Log.e(TAG, "ON RESUME: Output stream creation failed.", e);
                }
                String message = "$MODE1,0,0,0,0,0,0,0,0,0,4200#";
                byte[] msgBuffer = message.getBytes();
                try {
                    outStream.write(msgBuffer);

                } catch (IOException e) {
                    Log.e(TAG, "ON RESUME: Exception during write.", e);
                }
            }
        });

        mButtonFollow=(Button)findViewById(R.id.btnFollow);
        mButtonFollow.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    outStream = btSocket.getOutputStream();

                } catch (IOException e) {
                    Log.e(TAG, "ON RESUME: Output stream creation failed.", e);
                }
                String message = "$MODE3,0,0,0,0,0,0,0,0,0,4200#";
                byte[] msgBuffer = message.getBytes();
                try {
                    outStream.write(msgBuffer);

                } catch (IOException e) {
                    Log.e(TAG, "ON RESUME: Exception during write.", e);
                }
            }
        });


        if (D)
            Log.e(TAG, "+++ ON CREATE +++");
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }


        if (!mBluetoothAdapter.isEnabled()) {
            Toast.makeText(this, "Please enable your Bluetooth and re-run this program.", Toast.LENGTH_LONG).show();
            finish();
            return;

        }


        if (D)
            Log.e(TAG, "+++ DONE IN ON CREATE, GOT LOCAL BT ADAPTER +++");

    }


    @Override

    public void onStart() {

        super.onStart();

        if (D) Log.e(TAG, "++ ON START ++");
    }


    @Override

    public void onResume() {

        super.onResume();
        if (D) {
            Log.e(TAG, "+ ON RESUME +");
            Log.e(TAG, "+ ABOUT TO ATTEMPT CLIENT CONNECT +");

        }

        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);

        try {

            btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);

        } catch (IOException e) {

            Log.e(TAG, "ON RESUME: Socket creation failed.", e);

        }

        mBluetoothAdapter.cancelDiscovery();
        try {

            btSocket.connect();

            Log.e(TAG, "ON RESUME: BT connection established, data transfer link open.");

        } catch (IOException e) {

            try {
                btSocket.close();

            } catch (IOException e2) {

                Log .e(TAG,"ON RESUME: Unable to close socket during connection failure", e2);
            }

        }


// Create a data stream so we can talk to server.

        if (D)
            Log.e(TAG, "+ ABOUT TO SAY SOMETHING TO SERVER +");
/* try {
outStream = btSocket.getOutputStream();

} catch (IOException e) {
Log.e(TAG, "ON RESUME: Output stream creation failed.", e);
}


String message = "1";

byte[] msgBuffer = message.getBytes();

try {
outStream.write(msgBuffer);

} catch (IOException e) {
Log.e(TAG, "ON RESUME: Exception during write.", e);
}
*/

    }


    @Override

    public void onPause() {

        super.onPause();


        if (D)
            Log.e(TAG, "- ON PAUSE -");
        if (outStream != null) {
            try {
                outStream.flush();
            } catch (IOException e) {
                Log.e(TAG, "ON PAUSE: Couldn't flush output stream.", e);
            }

        }


        try {
            btSocket.close();
        } catch (IOException e2) {
            Log.e(TAG, "ON PAUSE: Unable to close socket.", e2);
        }

    }


    @Override

    public void onStop() {

        super.onStop();

        if (D) Log.e(TAG, "-- ON STOP --");

    }


    @Override

    public void onDestroy() {

        super.onDestroy();

        if (D) Log.e(TAG, "--- ON DESTROY ---");

    }

}


