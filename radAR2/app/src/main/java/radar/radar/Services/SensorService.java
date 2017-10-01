package radar.radar.Services;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import io.reactivex.Observable;

/**
 * Created by kenneth on 1/10/17.
 */

enum Type {
    PITCH, AZIMUTH
}

class PitchOrAzimuth {
    double value;
    Type type;

    public PitchOrAzimuth(double value, Type type) {
        this.value = value;
        this.type = type;
    }
}

public class SensorService {
    SensorManager sensorManager;
    SensorEventListener sensorEventListener;

    public Observable<PitchOrAzimuth> sensorUpdates;
    public Observable<Double> pitchUpdates;
    public Observable<Double> azimuthUpdates;

    // sensors
    Sensor magneticSensor;
    Sensor rotationSensor;

    // values
    private float[] mOrientationAngles = new float[3];  // for compass
    private float[] mRotationMatrix = new float[9];     // for compass
    private float[] mOrientationAngles2 = new float[3]; // for rotation sensor
    private float[] mRotationMatrix2 = new float[9];    // for rotation sensor

    public SensorService(SensorManager sensorManager) {
        assert(sensorManager != null);

        this.sensorManager = sensorManager;
        magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);

        // set up observables
        sensorUpdates = Observable.create(emitter -> {
            SensorFilter sensorFilter = new SensorFilter();

            sensorEventListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    if (event.sensor == rotationSensor) {
                        // Transform rotation matrix into azimuth/pitch/roll
                        SensorManager.getRotationMatrixFromVector(mRotationMatrix2, event.values);
                        SensorManager.remapCoordinateSystem(mRotationMatrix2, SensorManager.AXIS_X,
                                SensorManager.AXIS_Z, mRotationMatrix2);
                        SensorManager.getOrientation(mRotationMatrix2, mOrientationAngles2);

                        float[] orientation = new float[3];
                        SensorManager.getOrientation(mRotationMatrix2, orientation);

                        double pitch = -1 * Math.toDegrees(orientation[1]); // TODO needs -1 on OnePlus 5
                        pitch = sensorFilter.updateAndGetSmotothed(pitch);

                        emitter.onNext(new PitchOrAzimuth(pitch, Type.PITCH));
                    }

                    if (event.sensor == magneticSensor) {
                        SensorManager.getRotationMatrixFromVector(mRotationMatrix, event.values);
                        SensorManager.remapCoordinateSystem(mRotationMatrix, SensorManager.AXIS_X,
                                SensorManager.AXIS_Z, mRotationMatrix);
                        SensorManager.getOrientation(mRotationMatrix, mOrientationAngles);

                        double azimuth = Math.toDegrees(mOrientationAngles[0]);
                        azimuth = sensorFilter.updateAndGetSmotothed(azimuth);

                        emitter.onNext(new PitchOrAzimuth(azimuth, Type.AZIMUTH));
                    }

                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int i) {

                }
            };

            registerSensorEventListener();
        });

        pitchUpdates = sensorUpdates.filter((pitchOrAzimuth -> pitchOrAzimuth.type == Type.PITCH))
                                    .map((pitchOrAzimuth -> pitchOrAzimuth.value));

        azimuthUpdates = sensorUpdates.filter((pitchOrAzimuth -> pitchOrAzimuth.type == Type.AZIMUTH))
                                      .map((pitchOrAzimuth -> pitchOrAzimuth.value));

    }

    public void unregisterSensorEventListener() {
        sensorManager.unregisterListener(sensorEventListener);
    }

    public void registerSensorEventListener() {
        sensorManager.registerListener(sensorEventListener, rotationSensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(sensorEventListener, magneticSensor, SensorManager.SENSOR_DELAY_GAME);
    }
}
