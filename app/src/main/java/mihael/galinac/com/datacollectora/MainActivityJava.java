package mihael.galinac.com.datacollectora;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;


/**
 * Created by galin on 07-Apr-18.
 */

public class MainActivityJava extends Activity implements SensorEventListener {

//        private static final String STRING_ARRAY_SAMPLE = "./string-array-sample.csv";

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static boolean permissionGranted;

    private String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
    private static final String fileName = "AnalysisData.csv";
    private String filePath = baseDir + File.separator + fileName;

    private File file;
    FileWriter fileWriter;

    private SensorManager sensorManager;
    Sensor sensorLinearAcceleration;
    Sensor sensorGyroscope;

    private CSVWriter csvWriter;

    private TextView textViewData;
    private Button stopButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        verifyStoragePermissions(this);

        textViewData = findViewById(R.id.accData);

//        file = new File(filePath);
        try {
            fileWriter = new FileWriter(filePath, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        csvWriter = new CSVWriter(fileWriter,
                CSVWriter.DEFAULT_SEPARATOR,
                CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END);


        sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        sensorLinearAcceleration = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorGyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        stopButton = findViewById(R.id.stopButton);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(true){
                    Log.e("TAG", "onClick: bok");
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(
                this,
                sensorLinearAcceleration,
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(
                this,
                sensorGyroscope,
                SensorManager.SENSOR_DELAY_NORMAL);

        if (permissionGranted) {
            String[] headerRecord = {"Name", "Email", "Phone", "Country"};
            if (csvWriter != null) {
                csvWriter.writeNext(headerRecord);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
//        try {
//            fileWriter.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        if (permissionGranted) {
            if (sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
                Log.e("TAGACCELEROMETER", "poruke " + event.values[0]);
                float accX = event.values[0];
                float accY = event.values[1];
                float accZ = event.values[2];
                double accT = (Math.sqrt(accX * accX + accY * accY + accZ * accZ));

                textViewData.setText(String.valueOf(accT));
                if (accX > 1.00 || accY > 1.00 || accZ > 1.00 || accT > 2.00) {
                    csvWriter.writeNext(new String[]{
                            String.valueOf(accX),
                            String.valueOf(accY),
                            String.valueOf(accZ),
                            String.valueOf(accT)});
                }
            } else if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                Log.e("TAGGYROSCOPE", "poruke " + event.values[0]);
                float gyroX = event.values[0];
                float gyroY = event.values[1];
                float gyroZ = event.values[2];

                if (gyroX > 1 || gyroY > 1 || gyroZ > 1) {
                    csvWriter.writeNext(new String[]{
                            String.valueOf(gyroX),
                            String.valueOf(gyroY),
                            String.valueOf(gyroZ)});
                }
                textViewData.setText(String.valueOf(gyroX));
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * Checks if the app has permission to write to device storage
     * <p>
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        } else {
            permissionGranted = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionGranted = true;
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
//            case REQUEST_STORAGE: {
//
//            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
}
