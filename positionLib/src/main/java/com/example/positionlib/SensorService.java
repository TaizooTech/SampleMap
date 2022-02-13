package com.example.positionlib;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * MySensorManagerクラス
 */
public class SensorService implements SensorEventListener {

    private final SensorManager sensorManager;
    private float[] gravity = new float[3];
    private float[] geomagnetic = new float[3];
    private float azimuth = 0.0f;

    /**
     * コンストラクタ
     * @param context コンテキスト
     */
    public SensorService(Context context) {
        // SensorManagerのインスタンス取得
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // センサーのタイプに応じて値を取得
        switch (event.sensor.getType()) {
            case Sensor.TYPE_MAGNETIC_FIELD:
                geomagnetic = event.values.clone();
                break;
            case Sensor.TYPE_ACCELEROMETER:
                gravity = event.values.clone();
                break;
        }

        // 地磁気と加速度の両方の値が揃っていない場合、方位角の算出処理をスキップ
        if (null == geomagnetic || null == gravity) {
            return;
        }

        float[] R  = new float[16];     // 回転行列Rの値を格納する
        float[] value = new float[3];   // 方位角、ピッチ、ロールの回転角を格納する配列

        // 地磁気と加速度の値から回転行列を求める
        SensorManager.getRotationMatrix(R, null, gravity, geomagnetic);

        // 回転行列に基づいて方位角と傾きを算出
        SensorManager.getOrientation(R, value);

        // 方位角をラジアンから度に変換
        azimuth = (float) (value[0] * 180 / Math.PI);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    /**
     * センサー値の取得を開始する。
     */
    public void startSensor() {
        // 地磁気センサー値の取得を開始
        sensorManager.registerListener(
                this,
                sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL);

        // 加速度センサー値の取得を開始
        sensorManager.registerListener(
                this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    /**
     * センサー値の取得を停止する。
     */
    public void stopSensor() {
        sensorManager.unregisterListener(this);
    }

    /**
     * 方位角を取得する。
     * @return 方位角
     */
    public float getAzimuth() {
        return azimuth;
    }
}
