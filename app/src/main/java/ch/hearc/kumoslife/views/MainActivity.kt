package ch.hearc.kumoslife.views

import android.content.Intent
import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.media.AudioManager
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.view.View
import android.view.Window
import android.widget.*
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.json.JSONObject
import java.net.URL
import ch.hearc.kumoslife.views.statistics.StatisticsActivity
import ch.hearc.kumoslife.model.AppDatabase
import ch.hearc.kumoslife.model.statistics.Statistic
import ch.hearc.kumoslife.modelview.ShopViewModel
import ch.hearc.kumoslife.modelview.StatisticViewModel
import java.util.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.time.ExperimentalTime
import ch.hearc.kumoslife.views.shop.ShopActivity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import java.util.concurrent.Executors
import android.widget.Toast
import ch.hearc.kumoslife.*
import java.util.concurrent.ExecutorService
import android.media.MediaRecorder
import android.os.*
import kotlin.math.log10
import java.io.File
import java.io.IOException
import androidx.lifecycle.Observer

class MainActivity : AppCompatActivity()
{
    private val resPath: String = "android.resource://ch.hearc.kumoslife/"
    private val recordFileName: String = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + File.separator + "audiorecordtest.3gp"
    private val TAG: String = MainActivity::class.java.name
    private val LOCATION_PERMISSION_REQ_CODE = 1000
    private val REQUEST_PERM_ACCESS = 1
    private val MINIGAME_REQUEST_CODE = 1
    val API = "9d783bddf8b3eaa718e7d926a18ccb1c"     // API key used : allows 60 calls per minute

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var actualTime: LocalDateTime = LocalDateTime.MIN

    //Use of Executor and handler to replace AsyncTasks
    private val weatherExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    private val weatherHandler = Handler(Looper.getMainLooper())

    private var mediaRecorder: MediaRecorder? = null
    private val referenceAmplitude = 0.0001
    private var currentAmplitude = 0
    private val amplitudes: MutableList<Double> = MutableList(5) { 0.0 }

    private lateinit var bgVideoView: VideoView
    private lateinit var kumofragment: KumoFragment

    private lateinit var statisticViewModel: StatisticViewModel

    private val buttonList: LinkedList<Button> = LinkedList<Button>()
    private var isLightOn = true

    private var eyes = KumosEyes.HAPPY

    @ExperimentalTime
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        // Remove title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        supportActionBar?.hide()

        setContentView(R.layout.activity_main)

        // Background video initialization
        bgVideoView = findViewById(R.id.mainBgVideo)
        bgVideoView.setVideoPath(resPath + R.raw.day)
        bgVideoView.setAudioFocusRequest(AudioManager.AUDIOFOCUS_NONE)
        bgVideoView.setOnPreparedListener { mp ->
            mp.isLooping = true
            mp.setVolume(0.0F, 0.0F)    // no need of volume
        }

        createButtons()

        // Data base initialisation
        AppDatabase.getInstance(this)
        statisticViewModel = StatisticViewModel.getInstance(this)
        ShopViewModel.getInstance(this)

        // Luca.C - 28.10.2021 : initialize fused location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Init kumo fragment
        kumofragment = supportFragmentManager.findFragmentById(R.id.mainKumoFragment) as KumoFragment
        kumofragment.init()
        kumofragment.changeKumosShape(KumoColor.WHITE, KumosEyes.HAPPY, KumoMouth.HAPPY)

        updateLightValues()
        updateFeelings()
        updateBackground()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MINIGAME_REQUEST_CODE)
        {
            if (resultCode == RESULT_OK)
            {
                if (data != null && data.extras != null)
                {
                    val returnedData = data.extras!!.get(MinigameActivity.MINIGAME_COLLECTED_ID) as Int
                    //Toast.makeText(this, "Collected $returnedData unit(s) of FROOTS", Toast.LENGTH_SHORT).show()
                    addMoney(returnedData)

                    val activityStat: Statistic? = statisticViewModel.getStatisticByName("Activity")
                    if (activityStat != null)
                    {
                        Log.i(TAG, "Need for Actvitiy has been decreased")
                        statisticViewModel.decrease(10.0, activityStat)
                    }

                    val sicknessStat: Statistic? = statisticViewModel.getStatisticByName("Sickness")
                    if (sicknessStat != null)
                    {
                        Log.i(TAG, "Sickness has been decreased") // "high health" actually represents an unealthy Kumo, the meaning is inveted
                        statisticViewModel.decrease(5.0, sicknessStat)
                    }
                }
            }
        }
    }

    override fun onResume()
    {
        super.onResume()
        bgVideoView.start()
    }

    override fun onPause()
    {
        super.onPause()
        stopRecording()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray)
    {
        var file = false
        var mic = false

        if (requestCode == REQUEST_PERM_ACCESS)
        {
            var i = 0
            while (i < grantResults.size)
            {
                if (permissions[i].compareTo(Manifest.permission.WRITE_EXTERNAL_STORAGE) == 0 && grantResults[i] == PackageManager.PERMISSION_GRANTED)
                {
                    file = true
                }
                else if (permissions[i].compareTo(Manifest.permission.RECORD_AUDIO) == 0 && grantResults[i] == PackageManager.PERMISSION_GRANTED)
                {
                    mic = true
                }
                i++
                Log.i(TAG, "Not enough permissions")
            }

            if (mic && file)
            {
                // permission was granted
                Log.i(TAG, "Both permissions has now been granted")
                startRecording()
            }
            else
            {
                // permission denied
                Log.i(TAG, "A permission was NOT granted")
            }
            return
        }
        else
        {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun updateFeelings()
    {
        statisticViewModel.getAllStatistics().observe(this, Observer { statistics ->
            Log.i(TAG, "Update feelings")
            for (stat in statistics)
            {
                val feelingBubble = findViewById<ImageView>(resources.getIdentifier("main${stat.name}Image", "id", packageName))
                val feelingImage = findViewById<ImageView>(resources.getIdentifier("main${stat.name}Logo", "id", packageName))
                if (stat.value >= 90)
                {
                    feelingBubble.visibility = View.VISIBLE
                    feelingImage.visibility = View.VISIBLE
                }
                else
                {
                    feelingBubble.visibility = View.INVISIBLE
                    feelingImage.visibility = View.INVISIBLE
                }
            }
        })
    }

    @ExperimentalTime
    private fun updateBackground()
    {
        //Directly fetch localisation at app startup
        val weatherTimerTask: TimerTask = object : TimerTask()
        {
            override fun run()
            {
                Log.i(TAG, "Launching weather & location task")
                getCurrentLocation()
            }
        }
        val weatherTimer = Timer()
        weatherTimer.scheduleAtFixedRate(weatherTimerTask, 0, 1000 * 60 * 10) //Each 10 minutes.
    }

    private fun updateLightValues()
    {
        val lightTimerTask: TimerTask = object : TimerTask()
        {
            override fun run()
            {
                val stat: Statistic? = statisticViewModel.getStatisticByName("Sleep")
                if (stat != null && !isLightOn)
                {
                    Log.i(TAG, "Decrease sleep value")
                    statisticViewModel.decrease(1.0, stat)
                }
            }
        }
        val timer = Timer()
        timer.scheduleAtFixedRate(lightTimerTask, 0, 10000)
    }

    private fun createButtons()
    {
        // To statistics
        val toStatisticsButton: Button = findViewById(R.id.mainToStatisticsButton)
        toStatisticsButton.setOnClickListener {
            intent = Intent(this, StatisticsActivity::class.java)
            Log.i(TAG, "to Statistics")
            Log.i(TAG, StatisticViewModel.getInstance().getAllStatistics().value.toString())
            startActivity(intent)
        }
        buttonList.add(toStatisticsButton)

        // To shop
        val toShopButton: Button = findViewById(R.id.mainToShopButton)
        toShopButton.setOnClickListener {
            intent = Intent(this, ShopActivity::class.java)
            startActivity(intent)
        }
        buttonList.add(toShopButton)

        // To Game
        val toMinigameButton = findViewById<Button>(R.id.mainToMinigameButton)
        toMinigameButton.setOnClickListener() {
            intent = Intent(this, MinigameActivity::class.java)
            startActivityForResult(intent, MINIGAME_REQUEST_CODE)
        }
        buttonList.add(toMinigameButton)

        // Start/Stop Yelling
        val yellButton = findViewById<Button>(R.id.mainYellButton)
        yellButton.setOnClickListener {
            if (mediaRecorder != null)
            {
                stopRecording()
                yellButton.text = "Yell"
            }
            else
            {
                getVoiceLevel()
                yellButton.text = "Stop"
            }
        }
        buttonList.add(yellButton)

        // Turn off/on light
        findViewById<Button>(R.id.mainLightButton).setOnClickListener {
            isLightOn = !isLightOn

            val lightBg: TextView = findViewById(R.id.mainLightBg)
            if (isLightOn)
            {
                for (button in buttonList)
                {
                    button.isEnabled = true
                }
                lightBg.visibility = View.INVISIBLE

                kumofragment.changeKumosShape(kumofragment.getColor(), eyes, kumofragment.getMouth())
            }
            else
            {
                for (button in buttonList)
                {
                    button.isEnabled = false
                }
                lightBg.visibility = View.VISIBLE

                kumofragment.changeKumosShape(kumofragment.getColor(), KumosEyes.SLEEPING, kumofragment.getMouth())
            }
        }
    }

    private fun startRecording()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO), REQUEST_PERM_ACCESS)
        }
        else
        {
            mediaRecorder = MediaRecorder()
            mediaRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
            mediaRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            mediaRecorder!!.setOutputFile(recordFileName)
            mediaRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            try
            {
                mediaRecorder!!.prepare()
            }
            catch (e: IOException)
            {
                Log.e(TAG, "media recorder prepare() failed")
            }
            mediaRecorder!!.start()
        }
    }

    private fun stopRecording()
    {
        mediaRecorder?.stop()
        mediaRecorder?.release()
        mediaRecorder = null
    }

    private fun addMoney(add: Int)
    {
        val preferences = getSharedPreferences("bag", 0)
        val editor = preferences.edit()
        editor.putInt("money", preferences.getInt("money", 0) + add).commit()
    }

    private fun getVoiceLevel()
    {
        startRecording()
        val handler = Handler()
        handler.postDelayed(object : Runnable
        {
            override fun run()
            {
                val amplitude: Double = getAmplitude()
                amplitudes[currentAmplitude] = amplitude

                val max = amplitudes.maxOrNull() ?: 0.0
                if (max > 170.0)
                {
                    Toast.makeText(applicationContext, "Stop yelling at Kumo, it's loud enough !", Toast.LENGTH_SHORT).show()
                    Log.i(TAG, "Stop yelling at Kumo !")
                }

                currentAmplitude += 1
                if (currentAmplitude == amplitudes.size)
                {
                    currentAmplitude = 0
                }

                handler.postDelayed(this, 1000)
            }
        }, 10)
    }

    private fun getAmplitude(): Double
    {
        return if (mediaRecorder != null)
        {
            val maxAmplitude: Double = mediaRecorder!!.maxAmplitude.toDouble()
            20 * log10(maxAmplitude / referenceAmplitude)
        }
        else
        {
            0.0
        }
    }

    @ExperimentalTime
    private fun getCurrentLocation()
    {
        // checking location permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            // request permission
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQ_CODE)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location -> // getting the last known or current location
            var newLocation: Location? = null
            if (location == null || location.accuracy > 100)
            {
                object : LocationCallback()
                {
                    override fun onLocationResult(locationResult: LocationResult?)
                    {
                        if (locationResult != null && locationResult.locations.isNotEmpty())
                        {
                            newLocation = locationResult.locations[0]
                        }
                        else
                        {
                            Toast.makeText(applicationContext, "Failed on getting current location", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            when
            {
                location !== null    ->
                {
                    latitude = location.latitude
                    longitude = location.longitude
                    actualTime = LocalDateTime.now()
                    launchExecutor()
                }
                newLocation !== null ->
                {
                    latitude = newLocation!!.latitude
                    longitude = newLocation!!.longitude
                    actualTime = LocalDateTime.now()
                    launchExecutor()
                }
                else                 ->
                {
                    Toast.makeText(this, "Failed on getting current location. Please try again later", Toast.LENGTH_SHORT).show()
                }
            }
        }.addOnFailureListener { Toast.makeText(this, "Failed on getting current location", Toast.LENGTH_SHORT).show() }
    }

    private fun launchExecutor()
    {
        weatherExecutor.execute {
            val response = weatherTaskExecution()
            weatherHandler.post {
                checkWeatherResponse(response)
            }
        }
    }

    private fun weatherTaskExecution(): String?
    {
        val response = try
        {
            URL("https://api.openweathermap.org/data/2.5/weather?lat=$latitude&lon=$longitude&appid=$API").readText(Charsets.UTF_8)
        }
        catch (e: Exception)
        {
            null
        }

        return response
    }

    private fun checkWeatherResponse(result: String?)
    {
        try
        {
            // Extracting JSON returned from the API
            val jsonObj = JSONObject(result)

            /*
            val main = jsonObj.getJSONObject("main")
            val sys = jsonObj.getJSONObject("sys")
            val wind = jsonObj.getJSONObject("wind")

            val updatedAt: Long = jsonObj.getLong("dt")
            val updatedAtText =
                "Updated at: " + SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(
                    Date(updatedAt * 1000)
                )
            val temp = main.getString("temp") + "°C"
            val tempMin = "Min Temp: " + main.getString("temp_min") + "°C"
            val tempMax = "Max Temp: " + main.getString("temp_max") + "°C"
            val pressure = main.getString("pressure")
            val humidity = main.getString("humidity")

            val sunrise: Long = sys.getLong("sunrise")
            val sunset: Long = sys.getLong("sunset")
            val windSpeed = wind.getString("speed")
            val weatherDescription = weather.getString("description")
            */

            val weather = jsonObj.getJSONArray("weather").getJSONObject(0)
            val weatherID = weather.getString("main")

            /* TO CHECK :
            - Thunderstorm
            - Drizzle
            - Rain
            - Snow
            - Mist
            - Smoke
            - Haze
            - Dust
            - Ash
            - Fog
            - Sand
            - Squall
            - Tornado
            - Clear
            - Clouds
            */

            // Getting the hours of the actual time to change the background
            val formatter = DateTimeFormatter.ofPattern("HH")
            val formatted = actualTime.format(formatter)

            val isDay = formatted.toInt() in 7..17 // We determine the day time between 7h and 16h

            // Depending on the API results, we will use the correct video
            Log.i(TAG, "weatherID : $weatherID")
            when (weatherID)
            {
                "Fog"  ->
                {
                    if (isDay) bgVideoView.setVideoPath(resPath + R.raw.day_fog)
                    else bgVideoView.setVideoPath(resPath + R.raw.night_fog)
                    kumofragment.changeKumosShape(KumoColor.GRAY, KumosEyes.SAD, KumoMouth.SAD)
                }
                "Mist" ->
                {
                    if (isDay) bgVideoView.setVideoPath(resPath + R.raw.day_fog)
                    else bgVideoView.setVideoPath(resPath + R.raw.night_fog)
                    kumofragment.changeKumosShape(KumoColor.GRAY, KumosEyes.SAD, KumoMouth.SAD)
                }
                "Rain" ->
                {
                    if (isDay) bgVideoView.setVideoPath(resPath + R.raw.rain)
                    else bgVideoView.setVideoPath(resPath + R.raw.rain_night)
                    kumofragment.changeKumosShape(KumoColor.GRAY, KumosEyes.SAD, KumoMouth.SAD)
                }
                "Snow" ->
                {
                    if (isDay) bgVideoView.setVideoPath(resPath + R.raw.snow_day)
                    else bgVideoView.setVideoPath(resPath + R.raw.snow_night)
                    kumofragment.changeKumosShape(KumoColor.WHITE, KumosEyes.HAPPY, KumoMouth.HAPPY)
                }
                else   ->
                {
                    if (isDay) bgVideoView.setVideoPath(resPath + R.raw.day)
                    else bgVideoView.setVideoPath(resPath + R.raw.night)
                    kumofragment.changeKumosShape(KumoColor.WHITE, KumosEyes.HAPPY, KumoMouth.HAPPY)
                }
            }
            eyes = kumofragment.getEyes()

            bgVideoView.start()
            Log.i(TAG, "Starting bgVideoView with .start()")
            //Toast.makeText(this, weatherID, Toast.LENGTH_SHORT).show()
        }
        catch (e: Exception)
        {
            Log.e(TAG, "Error on Json: $e")
        }
    }
}
