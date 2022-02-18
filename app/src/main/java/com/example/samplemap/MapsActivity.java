package com.example.samplemap;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.positionlib.Position;
import com.example.positionlib.PositionService;
import com.example.samplemap.MyDialogFragment.InfoContents;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.samplemap.databinding.ActivityMapsBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

/**
 * MapsActivityクラス
 */
public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback, PositionService.OnPositionListener, GoogleMap.OnMarkerClickListener,
        NavigationBarView.OnItemSelectedListener {

    private static final int MODE_GAME = 0;
    private static final int MODE_EDIT = 1;
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private Marker marker;
    private PositionService service;
    private LatLng tmpLatLng;
    private boolean isUpdateMapCenter = true;
    private int mode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnItemSelectedListener(this);

        service = new PositionService(this);
        service.setOnPositionListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!service.start()) {
            // パーミッションリクエスト
            requestPermissions(new String[] {ACCESS_FINE_LOCATION},1);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        service.stop();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sydney = new LatLng(-34, 151);
        marker = mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        mMap.setOnMarkerClickListener(this);
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoContents(@NonNull Marker marker) {

                // info_window_layout.xml のビューを生成
                View view = getLayoutInflater().inflate(R.layout.info_window_layout, null);

                // イメージビューを取得
                ImageView imgView = view.findViewById(R.id.imageView);

                InfoContents contents = (InfoContents) marker.getTag();
                if (null == contents) {
                    return null;
                }

                imgView.setImageResource(contents.resourceId);
                ((TextView) view.findViewById(R.id.tv_title)).setText(contents.title);
                ((TextView) view.findViewById(R.id.tv_snipet)).setText(contents.snippet);

                return view;
            }

            @Nullable
            @Override
            public View getInfoWindow(@NonNull Marker marker) {
                return null;
            }
        });

        // 長押しクリックイベントをセット
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(@NonNull LatLng latLng) {

                tmpLatLng = latLng;

                // アイコン選択ダイアログを表示
                MyDialogFragment dialogFragment = new MyDialogFragment();
                dialogFragment.setDialogFragmentListener(listener);
                dialogFragment.show(getSupportFragmentManager(), "custom_dialog");
            }
        });
    }

    private final MyDialogFragment.OnDialogFragmentListener listener =
            new MyDialogFragment.OnDialogFragmentListener() {
        @Override
        public void onDialogResult(InfoContents contents) {
            // 長押しクリックイベントをキャッチしたらマーカーを追加
            Marker itemMarker = mMap.addMarker(new MarkerOptions().position(tmpLatLng));
            itemMarker.setTag(contents);
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (1 != requestCode) {
            return;
        }

        // ユーザが許可してくれた場合は、位置情報の取得を開始する
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            service.start();
        } else {
            Toast.makeText(this, "Permission Error.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPositionResult(Position position) {

        if (mode != MODE_GAME) {
            return;
        }

        LatLng latlng = new LatLng(position.getLatitude(), position.getLongitude());
        marker.setPosition(latlng);

        if (isUpdateMapCenter) {
            CameraPosition cameraPos = new CameraPosition.Builder()
                    .target(latlng)
                    .zoom(20.0f)
                    .bearing(position.getAzimuth())
                    .tilt(0)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPos));
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (null != marker.getTag()) {
            return false;
        }

        isUpdateMapCenter = !isUpdateMapCenter;
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (R.id.navigation_game == item.getItemId()) {
            mode = MODE_GAME;
        } else {
            mode = MODE_EDIT;
            CameraPosition cameraPos = new CameraPosition.Builder()
                    .target(marker.getPosition())
                    .zoom(20.0f)
                    .bearing(0)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPos));
        }

        return true;
    }
}