package mihael.galinac.com.datacollectora;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.text.format.DateFormat;
import android.util.Log;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
 * Created by galin on 12-Apr-18.
 */

public class SensorService extends Service implements SensorEventListener {
    private static final int ONGOING_NOTIFICATION_ID = 5;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
    private static final String fileName = "AnalysisData.csv";
    private String filePath = baseDir + File.separator + fileName;

    private SensorManager sensorManager;
    Sensor sensorLinearAcceleration;
    Sensor sensorGyroscope;

    private File csvFile;
    FileWriter fileWriter;

    private CSVWriter csvWriter;

    private NotificationManager notificationManager;
//    private boolean isShowingForegroundNotification;

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);


        CharSequence name = "Ragav";
        String desc = "this is notific";
        // ovaj imp sam promijenio dodavši Compat
        int imp = NotificationManagerCompat.IMPORTANCE_HIGH;
        final String ChannelID = "my_channel_01";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(ChannelID, name,
                    imp);
            mChannel.setDescription(desc);
            mChannel.setLightColor(Color.CYAN);
            mChannel.canShowBadge();
            mChannel.setShowBadge(true);
            notificationManager.createNotificationChannel(mChannel);
        }
        final int ncode = 101;

        //Ubacio sam umjest n.contentIntent ova dva reda ispod, i onda setContentIntent(pendingIntent) u definiranju notifikacije, al izgleda da se nista nije promijenilo

        // Kad bi se nekako moglo ubacit u new Intent umjesto MainActivitJava.class na koje mjesto u toj klasi ga baca

//        Intent notificationIntent = new Intent(this, MainActivityJava.class);
//        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                new Intent(getApplicationContext(), MainActivityJava.class), PendingIntent.FLAG_UPDATE_CURRENT);

        Notification n = new NotificationCompat.Builder(this, ChannelID)
                .setContentTitle("Data Collector")
                .setContentText("Aplikacija je pokrenuta i prikuplja podatke")
                .setContentIntent(pendingIntent)

                .setBadgeIconType(R.drawable.ic_launcher)
                .setNumber(5)
                .setSmallIcon(R.drawable.ic_launcher_round)
                .setAutoCancel(true)
                .build();




        //OPENING ACTIVITY ON NOTIFICATION CLICK
//            n.contentIntent=  PendingIntent.getActivity(this, 0,
//                    new Intent(this, MainActivityJava.class), PendingIntent.FLAG_UPDATE_CURRENT);

        startForeground(ONGOING_NOTIFICATION_ID, n);
        //notificationManager.notify(0, n);
//        }
    }

    @Override
    // ovdje ulazi sve što će se pokrenuti kada se pozove onStartCommand
    public int onStartCommand(Intent intent, int flags, int startId) {
        //return super.onStartCommand(intent, flags, startId);

        try {
            fileWriter = new FileWriter(filePath, false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        csvWriter = new CSVWriter(fileWriter,
                CSVWriter.DEFAULT_SEPARATOR,
                CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END);

        String[] headerRecord = {"AccX", "AccY", "AccZ", "AccT", "AccTime", "GyroX", "GyroY", "GyroZ", "GyroTime"};
        if (csvWriter != null) {
            csvWriter.writeNext(headerRecord);
        }

        sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        sensorLinearAcceleration = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorGyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        sensorManager.registerListener(
                this,
                sensorLinearAcceleration,
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(
                this,
                sensorGyroscope,
                SensorManager.SENSOR_DELAY_NORMAL);

        return Service.START_STICKY;

    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        notificationManager.cancelAll();

        sensorManager.unregisterListener(this);
        try {
            if (fileWriter != null) {
                fileWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;


        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            Log.e("TAGACCELEROMETER", "poruke " + event.values[0]);
            float accX = event.values[0];
            float accY = event.values[1];
            float accZ = event.values[2];
            double accT = (Math.sqrt(accX * accX + accY * accY + accZ * accZ));
            //               float timeStamp = (event.timestamp)/1000000000;
            String timeA = (String) DateFormat.format("yyyy-MM-dd hh:mm:ss", new Date());
            //

            //textViewData.setText(String.valueOf(accT));
            if (accX > 14.00 || accY > 14.00 || accZ > 14.00 || accT > 14.00) {
                csvWriter.writeNext(new String[]{
                        String.valueOf(accX),
                        String.valueOf(accY),
                        String.valueOf(accZ),
                        String.valueOf(accT),
                        String.valueOf(timeA)});
            }
        } else if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            Log.e("TAGGYROSCOPE", "poruke " + event.values[0]);
            float gyroX = event.values[0];
            float gyroY = event.values[1];
            float gyroZ = event.values[2];
            //    float timeStampG = (event.timestamp)/1000000000;
            String timeG = (String) DateFormat.format("yyyy-MM-dd hh:mm:ss a", new Date());


            if (gyroX > 2 || gyroY > 2 || gyroZ > 2) {
                csvWriter.writeNext(new String[]{
                        "", "", "", "", "",
                        String.valueOf(gyroX),
                        String.valueOf(gyroY),
                        String.valueOf(gyroZ),
                        String.valueOf(timeG)
                });
            }
            //textViewData2.setText(String.valueOf(gyroX));
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
