package mihael.galinac.com.datacollectora;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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

    private File csvFile;
    FileWriter fileWriter;

    private SensorManager sensorManager;
    Sensor sensorLinearAcceleration;
    Sensor sensorGyroscope;

    private CSVWriter csvWriter;

    private TextView textViewData;
    private TextView textViewData2;
    private Button stopButton;
    private Button sendButton;

    // dodano sa: https://stackoverflow.com/questions/18929929/convert-timestamp-into-current-date-in-android/18930056
//    private Date getDate(long time) {
//        Calendar cal = Calendar.getInstance();
//        TimeZone tz = cal.getTimeZone();//get your local time zone.
//        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
//        sdf.setTimeZone(tz);//set time zone.
//        String localTime = sdf.format(new Date(time) * 1000);
//        Date date = new Date();
//        try {
//            date = sdf.parse(localTime);//get local date
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return date;
//    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        verifyStoragePermissions(this);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        textViewData = findViewById(R.id.accData);
        textViewData2 = findViewById(R.id.gyroData);

//        file = new File(filePath);
        try {
            fileWriter = new FileWriter(filePath, false);
        } catch(IOException e) {
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
                csvFile = new File(filePath);
//                try {
//                    CSVReader csvReader = new CSVReader( new FileReader( filePath ) );
//                } catch( FileNotFoundException e ) {
//                    e.printStackTrace();
//                }
            }
        } );
        sendButton = findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"mgalinac@geof.hr"});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Podaci s voznje");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Dopišite ako ste promijenili \n" +
                        "- tip bicikla: \n" + "- položaj pametnog telefona: ");
                if(!csvFile.exists() || !csvFile.canRead()) {
                    return;
                }
                Uri uri = Uri.fromFile(csvFile);
                emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(emailIntent, "Pick an Email provider") );
            }
        } );
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
            String[] headerRecord = {"AccX", "AccY", "AccZ", "AccT", "AccTime", "GyroX", "GyroY", "GyroZ", "GyroTime"};
            if (csvWriter != null) {
                csvWriter.writeNext(headerRecord);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener( this );
        try {
            if ( fileWriter != null ) {
                fileWriter.close();
            }
        } catch ( IOException e ) {
            e.printStackTrace();
        }
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
                float timeStamp = (event.timestamp)/1000000000;

                textViewData.setText(String.valueOf(accT));
                if (accX > 1.00 || accY > 1.00 || accZ > 1.00 || accT > 3.00) {
                    csvWriter.writeNext(new String[]{
                            String.valueOf(accX),
                            String.valueOf(accY),
                            String.valueOf(accZ),
                            String.valueOf(accT),
                            String.valueOf(timeStamp)});
                }
            } else if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                Log.e("TAGGYROSCOPE", "poruke " + event.values[0]);
                float gyroX = event.values[0];
                float gyroY = event.values[1];
                float gyroZ = event.values[2];
                float timeStampG = (event.timestamp)/1000000000;

                if (gyroX > 1 || gyroY > 1 || gyroZ > 1) {
                    csvWriter.writeNext(new String[]{
                            "", "", "", "", "",
                            String.valueOf(gyroX),
                            String.valueOf(gyroY),
                            String.valueOf(gyroZ),
                            String.valueOf(timeStampG)
                    });
                }
                textViewData2.setText(String.valueOf(gyroX));
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
