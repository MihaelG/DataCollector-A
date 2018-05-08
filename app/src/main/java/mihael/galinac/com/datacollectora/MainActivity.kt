package mihael.galinac.com.datacollectora

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.FileWriter
import java.io.IOException
import kotlin.math.sqrt
import android.support.v4.content.FileProvider


@Suppress("UNUSED_CHANGED_VALUE")
class MainActivity : AppCompatActivity(), SensorEventListener {

    lateinit var sensorManager: SensorManager

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }


    //var accX: Double
    //var accY: Double
    //var accZ: Double

    //private TextViewA aX, aY, aZ
    // private Sensor mySensor
    // private SensorManager SM

    //Creating sensor manager

    //"max values" on axes
    var accXm = 0.00
    var accYm = 0.00
    var accZm = 0.00
    var accTm = 0.0
    var gyroXm = 0.00
    var gyroYm = 0.00
    var gyroZm = 0.00

    // lists of accelerometer and gyroscope data
    val accX_list = arrayListOf(0.00)
    val accY_list = arrayListOf(0.00)
    val accZ_list = arrayListOf(0.00)
    val accT_list = arrayListOf(0.00)
    val gyroX_list = arrayListOf(0.00)
    val gyroY_list = arrayListOf(0.00)
    val gyroZ_list = arrayListOf(0.00)

    // preparation for writing in csv
    val CSV_HEADER_ACC = "ACC_X,ACC_Y,ACC_Z,ACC_T"
    val CSV_HEADER_GYRO = "GYRO_X,GYRO_Y,GYRO_Z"
    //val ACC_LIST = arrayListOf()
    //val GYRO_LIST = arrayListOf()

    @SuppressLint("SetTextI18n")
    override fun onSensorChanged(event: SensorEvent?) {
        if (event!!.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            val accX = event.values[0].toDouble()
            val accY = event.values[1].toDouble()
            val accZ = event.values[2].toDouble()
            //VALUE OF "TOTAL ACCELERATION"
            val accT = (sqrt(accX * accX + accY * accY + accZ * accZ)).toDouble()

            if (accT > accTm) accTm = accT.toDouble()
            if (accX > accXm) accXm = accX.toDouble()
            if (accY > accYm) accYm = accY.toDouble()
            if (accZ > accZm) accZm = accZ.toDouble()

            if (accX > 1.00 || accY > 1.00 || accZ > 1.00 || accT > 2.00) {
                accX_list.add(accX)
                accY_list.add(accY)
                accZ_list.add(accZ)
                accT_list.add(accT)
                //JEL OVO PRAVILO DEFINIRANJE IF PETLJE?
            }

            //JEL ME JEBENO JEBEŠ DA JE TREBALO KONVERTAT U DOUBLE ONO KAJ JE VEĆ BILO DOUBLE!?!??!?!?

//            accData.text = "X= $accX \n" +
//                    "Y=  $accY \n" +
//                    "Z=  $accZ \n" +
//                    "AT= $accT \n" +
//                    "AXmax= $accXm \n" +
//                    "AYmax= $accYm \n" +
//                    "AZmax= $accZm \n" +
//                    "ATmax= $accTm \n"
        }
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            val gyroX = event.values[0].toDouble()
            val gyroY = event.values[1].toDouble()
            val gyroZ = event.values[2].toDouble()

            if (gyroX > gyroXm) gyroXm = gyroX.toDouble()
            if (gyroY > gyroYm) gyroYm = gyroY.toDouble()
            if (gyroZ > gyroZm) gyroZm = gyroZ.toDouble()

            // if value is greather then XX then add to list
            if (gyroX > 1 || gyroY > 1 || gyroZ > 1) {
                gyroX_list.add(gyroX)
                gyroY_list.add(gyroY)
                gyroZ_list.add(gyroZ)
                //JEL OVO PRAVILO DEFINIRANJE IF PETLJE?
            }


//            gyroData.text = "X= $gyroX \n" +
//                    "Y=  $gyroY \n" +
//                    "Z=  $gyroZ \n" +
//                    "GyroXmax= $gyroXm \n" +
//                    "GyroYmax= $gyroYm \n" +
//                    "GyroZmax= $gyroZm \n"
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        /*
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorMagnetic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorEventListener = new SensorEventListener()
        */

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.registerListener(
                this,
                sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
                SensorManager.SENSOR_DELAY_NORMAL
        )
        sensorManager.registerListener(
                this,
                sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_NORMAL
        )
        stopButton.setOnClickListener {
            //ACC_LIST.add(accX_list, accY_list, accZ_list, accT_list)
            //GYRO_LIST.add(gyroX_list, gyroY_list, gyroZ_list)

            var fileWriter: FileWriter? = null
            // zašto try??????????????????
            try {
                //KAKO SPREMIT OVAJ FILE?!?!?!??!

                fileWriter = FileWriter("ACCs.csv")

                fileWriter.append(CSV_HEADER_ACC)
                fileWriter.append('\n')

                // KOLIKO IMA ČLANOVA U LISTI TOLKI ĆE BITI BROJAČ
                val acc_list_size = accX_list.size
                var a_counter = 0
                while (a_counter <= acc_list_size) {
                    fileWriter.append(accX_list[a_counter].toString())
                    fileWriter.append(',')
                    fileWriter.append(accY_list[a_counter].toString())
                    fileWriter.append(',')
                    fileWriter.append(accZ_list[a_counter].toString())
                    fileWriter.append(',')
                    fileWriter.append(accT_list[a_counter].toString())
                    fileWriter.append('\n')
                    a_counter++
                }

                val emailSelectorIntent = Intent(Intent.ACTION_SENDTO)
                emailSelectorIntent.data = Uri.parse("mailto:")

                val emailIntent = Intent(Intent.ACTION_SEND)
                emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("mgalinac@geof.hr"))
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Podaci s bicikliranja")
                emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                emailIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                emailIntent.selector = emailSelectorIntent

                emailIntent.putExtra(Intent.EXTRA_STREAM, fileWriter.toString() )

                if (emailIntent.resolveActivity(packageManager) != null)
                    startActivity(emailIntent)

                //println("Write CSV successfully!")
            } catch (e: Exception) {
                println("Writing CSV error!")
                e.printStackTrace()
            } finally {
                try {
//                    fileWriter!!.flush()
//                    fileWriter.close()
                } catch (e: IOException) {
                    println("Flushing/closing error!")
                    e.printStackTrace()
                }
            }


            //ovdje ubacit file objekt koji cu ubacit u liniju ispod
            // registriat ko service
//            val attachment = FileProvider.getUriForFile(this, "my_fileprovider", "ACCs.csv")


/*
        sendButton.setOnClickListener{
            val mailto = "mailto:mgalinac@geof.hr" +
                    "&subject" + Uri.encode("Email subject") +
                    "&body" + Uri.encode("Email text")
            val intent = Intent (Intent.ACTION_SENDTO)
            intent.data = Uri.parse(mailto)
*/
            run {
                //            val emailSelectorIntent = Intent(Intent.ACTION_SENDTO)
//            emailSelectorIntent.data = Uri.parse("mailto:")
//
//            val emailIntent = Intent(Intent.ACTION_SEND)
//            emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("mgalinac@geof.hr"))
//            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Podaci s bicikliranja")
//            emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//            emailIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
//            emailIntent.selector = emailSelectorIntent
//
//
//            //ovdje ubacit file objekt koji cu ubacit u liniju ispod
//            // registriat ko service
////            val attachment = FileProvider.getUriForFile(this, "my_fileprovider", "ACCs.csv")
//            emailIntent.putExtra(Intent.EXTRA_STREAM, attachment)
//
//            if (emailIntent.resolveActivity(packageManager) != null)
//                startActivity(emailIntent)
            }
        }

    }

}
