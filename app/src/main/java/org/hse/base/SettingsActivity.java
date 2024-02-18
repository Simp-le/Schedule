package org.hse.base;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class SettingsActivity extends AppCompatActivity implements SensorEventListener {

    //region Constants
    private static final String APP_PREFERENCES = "settings"; // file name
    private static final String APP_PREFERENCES_NAME = "name";
    private static final String APP_PREFERENCES_PROFILE_PHOTO = "profile_photo";
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE_CAMERA_PERMISSIONS = 100;
    private static final int ACTIVITY_REQUEST_CODE_CAMERA = 101;
    private final String[] PERMISSIONS_CAMERA = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    //endregion

    //region Fields
    private SharedPreferences sharedPreferences;
    private SensorManager sensorManager;
    private Sensor lightSensor;
    private ImageView profileImage;
    private EditText name;
    private TextView sensorLightText;
    private LinearLayout permissions;
    private String imagePath;

    // If user declined permission two times
    private boolean somePermissionsForeverDenied = false;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        profileImage = findViewById(R.id.profile_image);
        name = findViewById(R.id.text_name);
        sensorLightText = findViewById(R.id.text_light_value);
        permissions = findViewById(R.id.permissions_container);

        sharedPreferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (lightSensor != null) sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        else sensorLightText.setText(R.string.no_light_sensor);

        loadData();

        Button buttonPicture = findViewById(R.id.button_take_photo);
        Button buttonSave = findViewById(R.id.button_save);

        buttonPicture.setOnClickListener(v -> takePhoto());
        buttonSave.setOnClickListener(v -> saveSettings());
    }

    //region Main functions
    private void loadData() {
        if (sharedPreferences.contains(APP_PREFERENCES_NAME)) {
            name.setText(sharedPreferences.getString(APP_PREFERENCES_NAME, "NO NAME"));
        }

        if (sharedPreferences.contains(APP_PREFERENCES_PROFILE_PHOTO)) {
            String photo = sharedPreferences.getString(APP_PREFERENCES_PROFILE_PHOTO, "");
            loadImage(photo);
        }
        setPermissions();
    }

    private void setPermissions() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
        permissions.removeAllViews();
        for (Sensor sensor: sensorList) {
            TextView textView = new TextView(this);
            textView.setText(sensor.getName());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            textView.setLayoutParams(layoutParams);
            permissions.addView(textView);
        }
    }

    private void takePhoto() {
        if(!hasAllPermissions(this, PERMISSIONS_CAMERA)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_CAMERA, REQUEST_PERMISSIONS_REQUEST_CODE_CAMERA_PERMISSIONS);
        }
        else {
            dispatchTakePictureIntent();
        }
    }

    //endregion

    //region sub functions
    public static boolean hasAllPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(permissions.length == 0) {
            return;
        }

        // Check if all permissions in the "permissions" list are granted
        boolean allPermissionsGranted = true;
        if (grantResults.length > 0) {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
        }

        for(String permission: permissions) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                // returned true, so user already declined this permission once
                Log.e("denied", permission);
            } else {
                // So, not it may be that user set "never to ask again" or he didn't decline permission at all
                if(ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED){
                    Log.e("allowed", permission);
                } else {
                    Log.e("set to never ask again", permission);
                    somePermissionsForeverDenied = true;
                    break;
                }
            }
        }

        if (allPermissionsGranted) {
            if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE_CAMERA_PERMISSIONS) {
                dispatchTakePictureIntent();
            }
        } else {
            if(somePermissionsForeverDenied){
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("Permissions Required")
                        .setMessage("You have forcefully denied some of the required permissions " +
                                "for this action. If you need this functionality, please open settings, go to the permissions section and allow them.")
                        .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                        Uri.fromParts("package", getPackageName(), null));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setCancelable(false).create().show();
            }
        }
    }


    private void dispatchTakePictureIntent() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, ACTIVITY_REQUEST_CODE_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTIVITY_REQUEST_CODE_CAMERA && resultCode == RESULT_OK) {
            imagePath = getImagePath(data);
            loadImage(imagePath);
        }
    }

    private String getImagePath(Intent data) {
        Bitmap image = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
        File imageFile = saveImageFile(image);
        return imageFile.getAbsolutePath();
    }

    private void loadImage(String imagePath) {
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        profileImage.setImageBitmap(bitmap);
    }

    //endregion

    //region Saving data
    private void saveName() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(APP_PREFERENCES_NAME, name.getText().toString());
        editor.apply();
    }

    private void saveImagePath() {
        if (imagePath != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(APP_PREFERENCES_PROFILE_PHOTO, imagePath);
            editor.apply();
        }
    }

    private void saveSettings() {
        saveName();
        saveImagePath();
    }

    private File saveImageFile(Bitmap image) {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = null;

        // Generating name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        try {
            imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);
            FileOutputStream out = new FileOutputStream(imageFile);
            image.compress(Bitmap.CompressFormat.JPEG,100, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageFile;
    }
    //endregion

    //region override Sensor's methods
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            float currentValue = event.values[0];

            sensorLightText.setText(currentValue + " lux");
            Log.d("Sensor Changed", "onSensor Change :" + currentValue);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    //endregion
}
