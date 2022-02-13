package com.example.positionlib;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.annotation.NonNull;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class PositionService {

    private final Context context;
    private FusedLocationProviderClient flpClient = null;
    private LocationCallback locationCallback = null;
    private OnPositionListener listener;
    private SensorService sensorService;

    public interface OnPositionListener {
        void onPositionResult(Position position);
    }

    public PositionService(Context context) {
        this.context = context;
        sensorService = new SensorService(context);
    }

    public void setOnPositionListener(OnPositionListener listener) {
        this.listener = listener;
    }

    public boolean start() {

        if (!checkPermission()) {
            return false;
        }

        // 位置情報が変更された際に、通知を受け取るコールバックメソッドを定義
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (null != listener) {
                    Location location = locationResult.getLastLocation();

                    listener.onPositionResult(new Position(
                            location.getLatitude(),
                            location.getLongitude(),
                            sensorService.getAzimuth())
                    );
                }
            }
        };

        // 位置情報のリクエストを生成する
        LocationRequest request = LocationRequest.create();
        request.setInterval(1000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // 位置情報の更新をリクエストする
        flpClient = LocationServices.getFusedLocationProviderClient(context);
        flpClient.requestLocationUpdates(request, locationCallback, null);

        sensorService.startSensor();
        return true;
    }

    public void stop() {
        if (null != flpClient) {
            flpClient.removeLocationUpdates(locationCallback);
        }

        if (null != sensorService) {
            sensorService.stopSensor();
        }
    }

    private boolean checkPermission() {
        // アクセス許可チェック
        return context.checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
}
