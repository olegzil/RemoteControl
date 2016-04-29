package com.yo.deviceui;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yo.discoverymanagers.DiscoveryManager;
import com.yo.interfaces.ConnectedDeviceInterface;
import com.yo.interfaces.ResultNotificationInterface;
import com.yo.remotecontrol.MainActivity;
import com.yo.remotecontrol.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by oleg on 8/19/15.
 */
public class GestureDialog extends android.app.DialogFragment {
    private ArrayList<CustomListViewAdapter.ListModel> mChannelData = new ArrayList<>();
    ConnectedDeviceInterface mDevice;
    final static private String TAG="GESTURE";
    GestureDetector mGestureDetector;
    private Hashtable<String, String> mCommands=new Hashtable<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo);
        if (mDevice == null) {
            Bundle b = this.getArguments();
            initDeviceReference(b);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mDevice == null) {
            Bundle b = this.getArguments();
            initDeviceReference(b);
        }

        View view = (View) inflater.inflate(R.layout.gesture, container, false);
        TextView tv = (TextView) view.findViewById(R.id.text_gesture_title);
        String text = tv.getText().toString();
        text = String.format("%s: %s", text, mDevice.GetDeviceName());
        tv.setText(text);
        setupGestureArea(view, R.id.image_gesture_area);

        return view;
    }

    void setupGestureArea(View view, int resourceID)
    {
        ImageView iv = (ImageView) view.findViewById(resourceID);
        iv.setClickable(true);
        iv.setEnabled(true);
        iv.setFocusable(true);
        ColorDrawable cd = new ColorDrawable();
        cd.setColor(0xfff200ff);
        iv.setImageDrawable(cd);
        GestureHandler gd = new GestureHandler(view);
        mGestureDetector = new GestureDetector(view.getContext(), gd);
        mGestureDetector.setOnDoubleTapListener(gd);
        iv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mGestureDetector.onTouchEvent(event);
            }
        });
    }

    private void initDeviceReference(Bundle b) {
            Long deviceKey = b.getLong("device"); //get the device key
            mDevice = DiscoveryManager.getInstance().GetDeviceReference(deviceKey);
            if (b.getString("left") != null)
                mCommands.put("left", b.getString("left"));
            if (b.getString("right") != null)
                mCommands.put("right", b.getString("right"));
            if (b.getString("up") != null)
                mCommands.put("up", b.getString("up"));
            if (b.getString("down") != null)
                mCommands.put("down", b.getString("down"));
            if (b.getString("home") != null)
                mCommands.put("home", b.getString("home"));
            if (b.getString("enter") != null)
                mCommands.put("enter", b.getString("enter"));
        }
    private class GestureHandler implements GestureDetector.OnDoubleTapListener, GestureDetector.OnGestureListener
    {
        private View mView;
        private float mStartX=0, mStartY=0;
        private float mDistanceX=0.0f, mDistanceY = 0.0f;
        private int mSamples =0;
        private int mCommandTrigger=0;
        private static final int mNumberOfSamples = 5;
        private static final int mCommandThreashold=mNumberOfSamples*3;
        private boolean mCommandComplete=true;
        private ResultNotificationInterface mCallback = new ResultNotificationInterface() {
            @Override
            public void onError(JSONObject data) {

            }

            @Override
            public void onSuccess(JSONObject data) {
                mCommandComplete = true;
            }

            @Override
            public void done() {
            }
        };

        public GestureHandler(View view)
        {

            mView = view;
            Display display = MainActivity.mThisActivity.getWindowManager().getDefaultDisplay();
            DisplayMetrics dm = new DisplayMetrics();
            display.getMetrics(dm);
        }
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Display display = MainActivity.mThisActivity.getWindowManager().getDefaultDisplay();
            DisplayMetrics dm = new DisplayMetrics();
            display.getMetrics(dm);

            float viewX = new Float(mView.getWidth());
            float viewY = new Float(mView.getHeight());

            float displayX = new Float(dm.widthPixels);
            float displayY = new Float(dm.heightPixels);

            float rawX = e.getRawX();
            float rawY = e.getRawY();

            float x = viewX/displayX * rawX;
            float y = viewY/displayY * rawY;
            handleSingleTap(e, x, y);
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
//            Log.i("GestureHandler", "=-=-=-=-=-=-= onDoubleTap" );
            return false;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
//            Log.i("GestureHandler", "=-=-=-=-=-=-= onDoubleTapEvent" );
            return false;
        }

        @Override
        public boolean onDown(MotionEvent e) {
//            Log.i("GestureHandler", "=-=-=-=-=-=-= onDown" );
            mStartX = e.getAxisValue(MotionEvent.AXIS_X);
            mStartY = e.getAxisValue(MotionEvent.AXIS_Y);
//            String message=String.format("mStartX = %f mStartY = %f", mStartX, mStartY);
//            Log.i("=-=-=-=-=", message);
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
//            Log.i("GestureHandler", "=-=-=-=-=-=-= onShowPress" );
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (mSamples < mNumberOfSamples)
                ++mSamples;
            else
            {
                float deltaX = mStartX - e2.getAxisValue(MotionEvent.AXIS_X);
                float deltaY = mStartY - e2.getAxisValue(MotionEvent.AXIS_Y);
                mCommandTrigger += mSamples;
                mSamples = 0;

                String message = String.format("=-=-=-=-=-=-= delta x = %f\n=-=-=-=-=-=-= delta y = %f", deltaX, deltaY);
                String prefix = null;
                if (Math.abs(deltaX) > Math.abs(deltaY)) {
//                    prefix = "=-=-=-=-=-=-= x-axis dominant ";
//                    if (deltaX < 0)
//                        prefix += " moving right (negative)\n";
//                    else
//                        prefix += " moving left (positive)\n";
                    if (mCommandTrigger >= mCommandThreashold) {
                        mCommandTrigger = 0;
                        handleHorizontal(e2, deltaX);
                    }
                }
                else {
//                    prefix = "=-=-=-=-=-=-= y-axis dominant";
//                    if (deltaY < 0)
//                        prefix += " moving down (negative)\n";
//                    else
//                        prefix += " moving up (positive)\n";
                    if (mCommandTrigger >= mCommandThreashold) {
                        mCommandTrigger = 0;
                        handleVertical(e2, deltaY);
                    }
                }

//                Log.i("motion", prefix + " " + message);
                mStartX = e2.getAxisValue(MotionEvent.AXIS_X);
                mStartY = e2.getAxisValue(MotionEvent.AXIS_Y);
            }
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
//            Log.i("GestureHandler", "=-=-=-=-=-=-= onLongPress" );
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
        void handleHorizontal(MotionEvent e, float velocity)
        {
            if (velocity <= 0 && mCommandComplete) {
                mCommandComplete = false;
                mDevice.execute(mCommands.get("right"), null, mCallback, 0);
            }
            else {
                mCommandComplete = false;
                mDevice.execute(mCommands.get("left"), null, mCallback, 0);
            }
        }
        void handleVertical(MotionEvent e, float velocity)
        {
            if (velocity <= 0) {
                mCommandComplete = false;
                mDevice.execute(mCommands.get("down"), null, mCallback, 0);
            }
            else {
                mCommandComplete = false;
                mDevice.execute(mCommands.get("up"), null, mCallback, 0);
            }
        }
        void handleSingleTap(MotionEvent e, float x, float y)
        {
            if (x < 100.0f)
                mDevice.execute(mCommands.get("home"), null, null, 0);
            else
                mDevice.execute(mCommands.get("enter"), null, null, 0);
        }
    }
}