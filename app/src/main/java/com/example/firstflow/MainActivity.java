package com.example.firstflow;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.NonNull;

import android.util.Log;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.example.firstflow.fragment.CloudFragment;
import com.example.firstflow.fragment.ContactFragment;
import com.example.firstflow.fragment.GalleryFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    public static final String[] PERMISSIONS = new String[]{
        Manifest.permission.CALL_PHONE,
        Manifest.permission.READ_CONTACTS
    };

    BottomNavigationView btmNaviView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btmNaviView = findViewById(R.id.bottomNavigationView);
        btmNaviView.setSelectedItemId(R.id.tab_contact);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkVerify(PERMISSIONS);
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.home_ly, new ContactFragment())
                .commit();
        SettingListener();

    }

    // =================== 권한 요청 코드 ==================

    @TargetApi(Build.VERSION_CODES.M)
    public void checkVerify(String[] permissions) {
        Log.d("checkVerify", "checkVerify 진입");
        ArrayList<String> notAllowedPermissions = new ArrayList<>();

        for(String permissionName : permissions){
            if(checkSelfPermission(permissionName) != PackageManager.PERMISSION_GRANTED){
                // TODO : 거절했을 때 할 액션
                // if (shouldShowRequestPermissionRationale(permissionName)) {}

                notAllowedPermissions.add(permissionName);
            }
        }

        if(notAllowedPermissions.size() > 0){
            requestPermissions(notAllowedPermissions.toArray(new String[0]),1);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1 && grantResults.length > 0) {
                for (int i = 0; i < grantResults.length; ++i) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        // 하나라도 거부한다면.
                        new AlertDialog.Builder(this).setTitle("알림").setMessage("권한을 허용해주셔야 앱을 이용할 수 있습니다.")
                                .setPositiveButton("종료", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        MainActivity.this.finish();
                                    }
                                }).setNegativeButton("권한 설정", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                                .setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                                        getApplicationContext().startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                    }
                                }).setCancelable(false).show();

                        return;
                    }
                }

        }
    }


    private void SettingListener() {
        btmNaviView.setOnItemSelectedListener(new TabSelectedListener());
    }

    class TabSelectedListener implements BottomNavigationView.OnItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.tab_contact: {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.home_ly, new ContactFragment())
                            .commit();
                    return true;
                }
                case R.id.tab_gallery: {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.home_ly, new GalleryFragment())
                            .commit();
                    return true;
                }
                case R.id.tab_cloud: {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.home_ly, new CloudFragment())
                            .commit();
                    return true;
                }
            }

            return false;
        }
    }

}