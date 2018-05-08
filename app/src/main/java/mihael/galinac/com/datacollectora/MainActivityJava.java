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
import android.support.v4.content.ContextCompat;
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

public class MainActivityJava extends Activity  {



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
//
    private File csvFile;
    FileWriter fileWriter;


    private TextView textViewData;
    private TextView textViewData2;
    private Button stopButton;
    private Button sendButton;
    private Button startButton;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        verifyStoragePermissions(this);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

//        textViewData = findViewById(R.id.accData);
//        textViewData2 = findViewById(R.id.gyroData);

//        file = new File(filePath);
        try {
            fileWriter = new FileWriter(filePath, false);
        } catch(IOException e) {
            e.printStackTrace();
        }

        final TextView startTxt = (TextView) findViewById(R.id.startTxt);
        final TextView stopTxt = findViewById(R.id.stopTxt);

        startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener(){


            //Ovdje ću pokrenut service, a planiram ga zaustavit na klik na stopButton
            @Override
            public void onClick(View v) {
                startButton.setVisibility(View.GONE);
                stopButton.setEnabled(true);
//                stopButton.setVisibility(View.VISIBLE);

                startTxt.setText("Mjerenje je započelo!");
                startTxt.setVisibility(View.VISIBLE);
                if (permissionGranted) {
                    //startService(new Intent(MainActivityJava.this, SensorService.class).putExtra("foreground", true));

                    // A OVO NE FUNKCIONIRA JER startForegroundService zahtjeva  API 26
                    Intent notificationIntent = new Intent(MainActivityJava.this, SensorService.class);
                    ContextCompat.startForegroundService(MainActivityJava.this, notificationIntent);
                }
            }
        });

        stopButton = findViewById(R.id.stopButton);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopButton.setVisibility(View.GONE);
                sendButton.setEnabled(true);


                startTxt.setVisibility(View.INVISIBLE);
                stopTxt.setVisibility(View.VISIBLE);
                stopTxt.setText("Mjerenje je završeno, možete poslati mail!");
                csvFile = new File(filePath);
                stopService(new Intent(MainActivityJava.this, SensorService.class));


            }
        } );
        sendButton = findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                startButton.setVisibility(View.VISIBLE);
                stopButton.setVisibility(View.VISIBLE);
                stopTxt.setVisibility(View.INVISIBLE);

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

    }

    @Override
    protected void onPause() {
        super.onPause();
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

                    finish();

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
