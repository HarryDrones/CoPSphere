package com.example.con10.copsphere;


import android.os.Bundle;
import android.widget.TextView;




import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;


import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;


import com.msppg.ATTITUDE_Handler;
import com.msppg.IMU_Handler;
import com.msppg.Parser;

import java.util.Set;

import processing.core.PApplet;

import static processing.core.PApplet.radians;

/* Created by con10 on 6/6/2018.
 */

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.msppg.ATTITUDE_Handler;
import com.msppg.IMU_Handler;
import com.msppg.Parser;

import java.util.Set;

import processing.core.PApplet;

import static processing.core.PApplet.radians;

public class MainActivity extends Activity implements OnClickListener {

    private static final String MAIN_FRAGMENT_TAG = "main_fragment";
    public static int[] DataString;
    //  private PfdRollPitchView mHUD;
    //  public quakes mHUD;
    private static UsbService usbService;
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            if (arg1.getAction().equals(UsbService.ACTION_USB_PERMISSION_GRANTED)) // USB PERMISSION GRANTED
            {
                Toast.makeText(arg0, "USB Ready", Toast.LENGTH_SHORT).show();
            } else if (arg1.getAction().equals(UsbService.ACTION_USB_PERMISSION_NOT_GRANTED)) // USB PERMISSION NOT GRANTED
            {
                Toast.makeText(arg0, "USB Permission not granted", Toast.LENGTH_SHORT).show();
            } else if (arg1.getAction().equals(UsbService.ACTION_NO_USB)) // NO USB CONNECTED
            {
                Toast.makeText(arg0, "No USB connected", Toast.LENGTH_SHORT).show();
            } else if (arg1.getAction().equals(UsbService.ACTION_USB_DISCONNECTED)) // USB DISCONNECTED
            {
                Toast.makeText(arg0, "USB disconnected", Toast.LENGTH_SHORT).show();
            } else if (arg1.getAction().equals(UsbService.ACTION_USB_NOT_SUPPORTED)) // USB NOT SUPPORTED
            {
                Toast.makeText(arg0, "USB device not supported", Toast.LENGTH_SHORT).show();
            }
        }
    };
    public PApplet fragment;
    int viewId = 0x1000;
    //  public static String msgBytes = new String ();
    private MyHandler mHandler;
    private final ServiceConnection usbConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            usbService = ((UsbService.UsbBinder) arg1).getService();
            usbService.setHandler(mHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            usbService = null;
        }
    };

    //Sends an attitude request if the current tab allows for it.
    //Sends an attitude request if the current tab allows for it.
    public static void sendAttitudeRequest() {

        MyHandler.sendRequest(MyHandler.attitude_request);
    }

    public static void sendImuRequest() {

        MyHandler.sendRequest(MyHandler.imu_request);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        //   setContentView(R.layout.activity_main);
        //   mHUD = new PfdRollPitchView(this);
        //    setContentView(mHUD);
        mHandler = new MyHandler(this);


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        FrameLayout frame = new FrameLayout(this);
        frame.setId(viewId);
        setContentView(frame, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        if (savedInstanceState == null) {

            //  fragment = new Scatter3D();
            fragment = new CoPSphere();
            // MyFragment obj = new MyFragment();
            //  fragment.setArguments(bundle);

            //  transaction.replace(R.id.fragment_single, fragment);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(frame.getId(), fragment, MAIN_FRAGMENT_TAG).commit();

        } else {
            fragment = (PApplet) getFragmentManager().findFragmentByTag(MAIN_FRAGMENT_TAG);

        }


    }

    @Override
    public void onResume() {
        super.onResume();
        setFilters();  // Start listening notifications from UsbService
        startService(UsbService.class, usbConnection, null); // Start UsbService(if it was not started before) and Bind it
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mUsbReceiver);
        unbindService(usbConnection);
    }

    private void startService(Class<?> service, ServiceConnection serviceConnection, Bundle extras) {
        if (!UsbService.SERVICE_CONNECTED) {
            Intent startService = new Intent(this, service);
            if (extras != null && !extras.isEmpty()) {
                Set<String> keys = extras.keySet();
                for (String key : keys) {
                    String extra = extras.getString(key);
                    startService.putExtra(key, extra);
                }
            }
            startService(startService);
        }
        Intent bindingIntent = new Intent(this, service);
        bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void setFilters() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbService.ACTION_USB_PERMISSION_GRANTED);
        filter.addAction(UsbService.ACTION_NO_USB);
        filter.addAction(UsbService.ACTION_USB_DISCONNECTED);
        filter.addAction(UsbService.ACTION_USB_NOT_SUPPORTED);
        filter.addAction(UsbService.ACTION_USB_PERMISSION_NOT_GRANTED);
        registerReceiver(mUsbReceiver, filter);
    }

    @Override
    public void onClick(View v) {

    }

    public static class MyHandler extends Handler implements ATTITUDE_Handler, IMU_Handler {

        private static float[] Orientation = new float[3];
        private static float[] Data = new float[3];
        private static float[] ImuData = new float[9];
        private static double[] DcM = new double[9];
        private static float[] DCM = new float[9];
        //  public static float [] euler = new float [3];
        private static byte[] attitude_request;
        private static byte[] imu_request;
        private MainActivity mMainActivity;
        //  private DcmViewer mDcmViewer;
        private Parser parser;


        public MyHandler(MainActivity activity) {
            mMainActivity = activity;
            parser = new Parser();
            parser.set_ATTITUDE_Handler(this);
            parser.set_IMU_Handler(this);

            //  public static  orientation = new Short[] {0,0,0};
            attitude_request = parser.serialize_ATTITUDE_Request();
            imu_request = parser.serialize_IMU_Request();

        }

        //Sends the request so long as a usb is plugged in
        public static void sendRequest(byte[] request) {
            if (usbService != null) {
                usbService.write(request);
            }
        }

        public static float[] getOrientation() {
            //   setOrientation( short roll, short pitch, short yaw);

//       System.out.println(String.format("Roll: %d  Pitch: %d    Yaw: %d", Orientation[0], Orientation[1], Orientation[2]));
            return Orientation;
        }

        public static float[] getData() {


//        System.out.println(String.format("Roll: %d  Pitch: %d    Yaw: %d", Data[0], Data[1], Data[2]));
            return Data;
        }

        public static float[] getRotate() {
            System.out.println(String.format("M00: %d  M01: %d    M02: %d", (int) (DCM[0] * 1000), (int) (DCM[1] * 1000), (int) (DCM[2] * 1000)));
            return DCM;
        }

        public static float[] getImuData() {
            //   System.out.println(String.format("ACCX: %d  ACCY: %d    ACCZ: %d", (int) ImuData[0], (int) ImuData[1], (int) ImuData[2]));
            return ImuData;
        }

        // short roll, short pitch, short yaw
        public static void setOrientation(short roll, short pitch, short yaw) {
            Orientation[0] = roll;
            Orientation[1] = pitch;
            Orientation[2] = yaw;
            //   System.out.println(String.format("ROLL: %d  PITCH: %d    YAW: %d", (int)Orientation[0] , (int)Orientation[1], (int)Orientation[2] ));

        }

        public static void setData(short roll, short pitch, short yaw) {
            Data[0] = (float) roll / 10;
            Data[1] = (float) pitch / 10;
            Data[2] = (float) yaw;

        }

        public static void setImuData(short accx, short accy, short accz, short gyrx, short gyry, short gyrz, short magx, short magy, short magz) {


            ImuData[0] = (float) accx;
            ImuData[1] = (float) accy;
            ImuData[2] = (float) accz;

            ImuData[3] = (float) gyrx;
            ImuData[4] = (float) gyry;
            ImuData[5] = (float) gyrz;

            ImuData[6] = (float) magx;
            ImuData[7] = (float) magy;
            ImuData[8] = (float) magz;

        }

        /**
         * this conversion uses NASA standard aeroplane conventions as described on page:
         * http://www.euclideanspace.com/maths/geometry/rotations/euler/index.htm
         * Coordinate System: right hand
         * Positive angle: right hand
         * Order of euler angles: heading first, then attitude, then bank
         * matrix row column ordering:
         * [m00 m01 m02]
         * [m10 m11 m12]
         * [m20 m21 m22]
         * sa = sin(attitude)
         * ca = cos(attitude) //pitch
         * sb = sin(bank)
         * cb = cos(bank)  //roll
         * sh = sin(heading)
         * ch = cos(heading)  //yaw
         */
        public static void setRotate(short roll, short pitch, short yaw) {
            // Assuming the angles are in radians.
            double Yaw = radians(yaw);
            double Pitch = radians(pitch / 10);
            double Roll = radians(roll / 10);
            double cb = (Math.cos((Roll)));
            double sb = (Math.sin((Roll)));
            double ca = (Math.cos((Pitch)));
            double sa = (Math.sin((Pitch)));
            double ch = (Math.cos((Yaw)));
            double sh = (Math.sin((Yaw)));


     /*   DcM[0] = (ch * ca);
        DcM[1] = (sh*sb - ch*sa*cb);
        DcM[2] = (ch*sa*sb + sh*cb);
        DcM[3] =  sa;
        DcM[4] = (ca*cb);
        DcM[5] = (-ca*sb);
        DcM[6] = (-sh*ca);
        DcM[7] = (sh*sa*cb + ch*sb);
        DcM[8] = (-sh*sa*sb + ch*cb); */

            DcM[0] = (ch * ca);
            DcM[1] = (sh * sb - ch * sa * cb);
            DcM[2] = (ch * sa * sb + sh * cb);

            DcM[3] = sa;
            DcM[4] = (ca * cb);
            DcM[5] = (-ca * sb);

            DcM[6] = (-sh * ca);
            DcM[7] = (sh * sa * cb + ch * sb);
            DcM[8] = (-sh * sa * sb + ch * cb);

            DCM[0] = (float) (DcM[0] * 1000); //0,0
            DCM[1] = (float) (DcM[1] * 1000); //0,1
            DCM[2] = (float) (DcM[2] * 1000); //0,2

            DCM[3] = (float) (DcM[3] * 1000); //1,0
            DCM[4] = (float) (DcM[4] * 1000); //1,1
            DCM[5] = (float) (DcM[5] * 1000); //1,2

            DCM[6] = (float) (DcM[6] * 1000); //2,0
            DCM[7] = (float) (DcM[7] * 1000); //2,1
            DCM[8] = (float) (DcM[8] * 1000); //2,2

                 /*    stroke(0==view ? 0x330000FF : #0000FF);      // X - blue
    draw_vector(dcm[0],dcm[3],dcm[6]);
      stroke(0==view ? 0x33FF0000 : #FF0000);    // Y - red
    draw_vector(dcm[1],dcm[4],dcm[7]);
    stroke(0==view ? 0x3300FF00 : #00FF00);      // Z - green
    draw_vector(dcm[2],dcm[5],dcm[8]);
            *
             *
             * */

        }


        public void handle_ATTITUDE(short roll, short pitch, short yaw) {

            setOrientation(roll, pitch, yaw);
            setData(roll, pitch, yaw);
            setRotate(roll, pitch, yaw);


            sendAttitudeRequest();  //sends a request to continue the call and response.
        }

        public void handle_IMU(short accx, short accy, short accz, short gyrx, short gyry, short gyrz, short magx, short magy, short magz) {


            setImuData(accx, accy, accz, gyrx, gyry, gyrz, magx, magy, magz);

            sendImuRequest();  //sends a request to continue the call and response.
        }


        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case UsbService.MESSAGE_FROM_SERIAL_PORT:

                    byte[] data = (byte[]) msg.obj;

                    for (byte b : data) {
                        parser.parse(b);
                    }
                    break;
            }
        }

//    public void setDcmViewer(DcmViewer dcmViewer) {
        //       mDcmViewer = dcmViewer;
        //   }
    }


}
