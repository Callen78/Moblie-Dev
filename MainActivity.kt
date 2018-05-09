package com.example.ceocarlallen.recorder


import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Button
import android.widget.Toast
import java.io.IOException


class MainActivity<Throws> : AppCompatActivity(), View.OnClickListener {
    //global variables
    private var myAudioRecorder: MediaRecorder? = null
    private var output: String? = null
    private var start: Button? = null
    private var stop: Button? = null
    private var play: Button? = null


    private var permissionToRecordAccepted = false
    private var permissionToWriteAccepted = false
    private val permissions = arrayOf("android.permission.RECORD_AUDIO", "android.permission.WRITE_EXTERNAL_STORAGE")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Had to request for code
        val requestCode = 200
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode)
        }

        //creates variables for buttons
        start = findViewById(R.id.button1) as Button
        stop = findViewById(R.id.button2) as Button
        play = findViewById(R.id.button3) as Button


        //sets listners for button
        start!!.setOnClickListener(this)
        stop!!.setOnClickListener(this)
        play!!.setOnClickListener(this)


        //to get buttons to dislay one at a times
        stop!!.isEnabled = false
        play!!.isEnabled = false

        output = Environment.getExternalStorageDirectory().absolutePath + "/myrecording.3gp"


        myAudioRecorder = MediaRecorder()
        myAudioRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        myAudioRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        myAudioRecorder!!.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB)
        myAudioRecorder!!.setOutputFile(output)

    }

    override fun onClick(v: View) {

        when (v.id) {

            R.id.button1 -> start(v)
            R.id.button2 -> stop(v)
            R.id.button3 -> try {
                play(v)
            } catch (e: IOException) {
                Log.i("IOException", "Error in play")
            }

            else -> {
            }
        }

    }

    //method that gets permission

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            200 -> {
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                permissionToWriteAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
            }
        }
        if (!permissionToRecordAccepted) super@MainActivity.finish()
        if (!permissionToWriteAccepted) super@MainActivity.finish()


    }


//start function to start the recording
    fun start(view: View) {
        try {
            myAudioRecorder!!.prepare()
            myAudioRecorder!!.start()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        start!!.isEnabled = false
        stop!!.isEnabled = true
        Toast.makeText(applicationContext, "Recording started", Toast.LENGTH_SHORT).show()
    }

    fun stop(view: View) {
        myAudioRecorder!!.stop()
        myAudioRecorder!!.release()
        myAudioRecorder = null
        stop!!.isEnabled = false
        play!!.isEnabled = true
        Toast.makeText(applicationContext, "Audio recorded successfully", Toast.LENGTH_SHORT).show()

    }


    //  @Throws(IllegalArgumentException::class, SecurityException::class, IllegalStateException::class, IOException::class)
    fun play(view: View) {
        val m = MediaPlayer()
        try {
            m.setDataSource(output)
            m.prepare()
            m.start()
        }
        finally {
            Toast.makeText(applicationContext, "Playing audio", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        val RECORD_AUDIO = 0
    }
}

