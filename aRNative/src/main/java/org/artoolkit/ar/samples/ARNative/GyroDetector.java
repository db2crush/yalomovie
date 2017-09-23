package org.artoolkit.ar.samples.ARNative;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;

/**
 * Created by erunn on 2017-09-23.
 */

public class GyroDetector implements SensorEventListener{
    private static final int DETECTION_INTERVAL = 1000;     // 1000 ms
    private static final float DETECTION_VALUE = 1.7f;      // 1.7 rad/s


    private boolean mIsEnable;

    private SensorManager mSensorManager;
    private boolean mIsStartSensor;
    private ISwingDetectListener mListener;

    private final Handler mHandler;


    interface ISwingDetectListener {
        void onChanged(float gz);
    }

    GyroDetector(Context context) {
        mIsStartSensor = false;

        if (context instanceof ISwingDetectListener) {
            mListener = (ISwingDetectListener) context;
            mHandler = new Handler();
            mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
            mIsEnable = false;
        } else {
            throw new ClassCastException("must implement ISwingListener");
        }
    }





    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            if (mListener != null) {

                mListener.onChanged( event.values[2]);

                if (mIsEnable) {
                }
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    void startSensor() {
        if (!mIsStartSensor) {
            if (mSensorManager != null) {
                Sensor gyro = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
                if (gyro != null) {
                    mIsStartSensor = mSensorManager.registerListener(this, gyro, SensorManager.SENSOR_DELAY_NORMAL);
                }
            }
            if (mIsStartSensor) {
                mIsEnable = mIsStartSensor;
            }
        }
    }

    void stopSensor() {
        if (mIsStartSensor) {
            if (mSensorManager != null) {
                mSensorManager.unregisterListener(this);
                mIsStartSensor = false;
            }
        }
    }

    private void startIntervalTimer() {
        if (mHandler != null) {
            mHandler.postDelayed(mTimeout, DETECTION_INTERVAL);
        }
    }

    private final Runnable mTimeout = new Runnable() {
        @Override
        public void run() {
            mIsEnable = true;
        }
    };
}
