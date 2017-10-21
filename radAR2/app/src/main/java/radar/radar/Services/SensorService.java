package radar.radar.Services;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import io.reactivex.Observable;


public class SensorService {
    SensorEventListener azimuthEventListener;
    SensorEventListener pitchEventListener;
    SensorManager sensorManager;

    public Observable<Double> pitchUpdates;
    public Observable<Double> azimuthUpdates;

    public Observable<Double> getPitchUpdates() {
        return pitchUpdates;
    }

    public Observable<Double> getAzimuthUpdates() {
        return azimuthUpdates;
    }

    // sensors
    Sensor magneticSensor;
    Sensor rotationSensor;

    public SensorService(SensorManager sensorManager) {
        assert (sensorManager != null);

        this.sensorManager = sensorManager;
        magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);

        if (rotationSensor == null) {
            // gyro not supported
            Log.w("sensorService", "gyro not supported");
        }

        azimuthUpdates = Observable.create(emitter -> {
            float[] mOrientationAngles = new float[3];  // for compass
            float[] mRotationMatrix = new float[9];     // for compass

            azimuthEventListener = new SensorEventListener() {

                @Override
                public void onSensorChanged(SensorEvent sensorEvent) {
                    if (sensorEvent.sensor == magneticSensor) {
                        SensorManager.getRotationMatrixFromVector(mRotationMatrix, sensorEvent.values);
                        SensorManager.remapCoordinateSystem(mRotationMatrix, SensorManager.AXIS_X,
                                SensorManager.AXIS_Z, mRotationMatrix);
                        SensorManager.getOrientation(mRotationMatrix, mOrientationAngles);

                        double azimuth = Math.toDegrees(mOrientationAngles[0]);
                        azimuth = azimuth % 360d;    // azimuth can be less than -360 or 360
                        if (azimuth < 0.0d) {   // normalise
                            azimuth += 360d;
                        }

                        emitter.onNext(azimuth);
                    }

                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int i) {

                }
            };

            sensorManager.registerListener(azimuthEventListener, magneticSensor, SensorManager.SENSOR_DELAY_FASTEST);
        });

        pitchUpdates = Observable.create(emitter2 -> {
            SensorFilter filter = new SensorFilter();
            float[] mOrientationAngles2 = new float[3]; // for rotation sensor
            float[] mRotationMatrix2 = new float[9];    // for rotation sensor

            pitchEventListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent sensorEvent) {

                    if (sensorEvent.sensor == rotationSensor) {
                        // Transform rotation matrix into azimuth/pitch/roll
                        SensorManager.getRotationMatrixFromVector(mRotationMatrix2, sensorEvent.values);
                        SensorManager.remapCoordinateSystem(mRotationMatrix2, SensorManager.AXIS_X,
                                SensorManager.AXIS_Z, mRotationMatrix2);
                        SensorManager.getOrientation(mRotationMatrix2, mOrientationAngles2);

                        float[] orientation = new float[3];
                        SensorManager.getOrientation(mRotationMatrix2, orientation);

                        double pitch = Math.toDegrees(orientation[1]);
                        pitch = filter.updateAndGetSmoothed(pitch);
                        emitter2.onNext(pitch);
                    }

                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int i) {

                }
            };
            sensorManager.registerListener(pitchEventListener, rotationSensor, SensorManager.SENSOR_DELAY_FASTEST);

        });

    }

    /**
     * Unregisters the sensor event listeners.
     * To be used on onStart on the activity lifecycle.
     * Location tracking still can continue in the background via the LocationService.
     */
    public void unregisterSensorEventListener() {
        if (sensorManager != null && azimuthEventListener != null && pitchEventListener != null) {
            sensorManager.unregisterListener(azimuthEventListener);
            sensorManager.unregisterListener(pitchEventListener);
        }
    }

    /**
     * Reregisters the sensor event listeners.
     * To be used on onStart on the activity lifecycle for when the activity comes back into the
     * foreground.
     * Is actually called by Android on Activity creation, thus the null checks.
     * Location tracking still can continue in the background via the LocationService.
     */
    public void reregisterSensorEventListener() {
        if (sensorManager != null && azimuthEventListener != null && pitchEventListener != null) {
            sensorManager.registerListener(azimuthEventListener, magneticSensor, SensorManager.SENSOR_DELAY_FASTEST);
            sensorManager.registerListener(pitchEventListener, rotationSensor, SensorManager.SENSOR_DELAY_FASTEST);
        }
    }
}

