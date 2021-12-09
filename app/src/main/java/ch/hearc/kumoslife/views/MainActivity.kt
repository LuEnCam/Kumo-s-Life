package ch.hearc.kumoslife.views

import android.content.Intent
import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.view.View
import android.view.Window
import android.widget.*
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.json.JSONObject
import java.net.URL
import ch.hearc.kumoslife.views.statistics.StatisticsActivity
import androidx.work.WorkManager
import ch.hearc.kumoslife.R
import ch.hearc.kumoslife.SpriteView
import ch.hearc.kumoslife.model.AppDatabase
import ch.hearc.kumoslife.model.statistics.Statistic
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
import java.util.concurrent.ExecutorService

class MainActivity : AppCompatActivity()
{
    private val resPath: String = "android.resource://ch.hearc.kumoslife/"
    private val TAG: String = MainActivity::class.java.name
    private val LOCATION_PERMISSION_REQ_CODE = 1000
    val API = "9d783bddf8b3eaa718e7d926a18ccb1c"    //API key used : allows 60 calls per minute

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var actualTime: LocalDateTime = LocalDateTime.MIN

    //Use of Executor and handler to replace AsyncTasks
    private val weatherExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    private val weatherHandler = Handler(Looper.getMainLooper())

    private lateinit var bgVideoView: VideoView
    private lateinit var viewModel: StatisticViewModel
    private val buttonList: LinkedList<Button> = LinkedList<Button>()
    private val workManager = WorkManager.getInstance(application) // unused every time but needed to instantiate
    private var isLightOn = true

    @ExperimentalTime
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        // Remove title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        supportActionBar?.hide()

        setContentView(R.layout.activity_main)

        //
        val cloudSpriteView = findViewById<SpriteView>(R.id.kumo_spriteView)
        val eyesImageView = findViewById<ImageView>(R.id.eyes_imageView)
        val mouthImageView = findViewById<ImageView>(R.id.mouth_imageView)

        // Adding the drawables (images + gifs) to the ImageViews with Glade
        Glide.with(this).load(R.raw.eye).into(eyesImageView)
        Glide.with(this).load(R.drawable.mouth_happy_white).into(mouthImageView)

        // Background video initialization
        bgVideoView = findViewById(R.id.mainBgVideo)
        bgVideoView.setVideoPath(resPath + R.raw.day)
        bgVideoView.setOnPreparedListener { mp ->
            mp.isLooping = true
            mp.setVolume(0.0F, 0.0F)    // no need of volume
        }

        // To statistics
        val toStatisticsButton: Button = findViewById(R.id.mainToStatisticsButton)
        toStatisticsButton.setOnClickListener() {
            intent = Intent(this, StatisticsActivity::class.java)
            startActivity(intent)
        }
        buttonList.add(toStatisticsButton)

        // To shop
        val toShopButton: Button = findViewById(R.id.mainToShopButton)
        toShopButton.setOnClickListener() {
            intent = Intent(this, ShopActivity::class.java)
            startActivity(intent)
        }
        buttonList.add(toShopButton)

        // Turn off/on light
        findViewById<Button>(R.id.mainLightButton).setOnClickListener() {
            isLightOn = !isLightOn

            val lightBg: TextView = findViewById(R.id.mainLightBg)
            if (isLightOn)
            {
                for (button in buttonList)
                {
                    button.isEnabled = true
                }
                lightBg.visibility = View.INVISIBLE
            }
            else
            {
                for (button in buttonList)
                {
                    button.isEnabled = false
                }
                lightBg.visibility = View.VISIBLE
            }
        }

        // Data base initialization
        val db = AppDatabase.getInstance(applicationContext)
        viewModel = StatisticViewModel.getInstance(this)

        // Data base insertion of fresh new rows
        // viewModel.initDataBase()

        // Data base update every 15 mins
        // val statisticsWorker = PeriodicWorkRequestBuilder<StatisticsWorker>(15, TimeUnit.MINUTES).build()
        // workManager.enqueueUniquePeriodicWork("statisticsWorker", ExistingPeriodicWorkPolicy.KEEP, statisticsWorker)

        // Update light value
        val lightTimerTask: TimerTask = object : TimerTask()
        {
            override fun run()
            {
                val stat: Statistic? = viewModel.getStatisticByName("Sleep")
                if (stat != null && !isLightOn)
                {
                    Log.i(TAG, "Decrease sleep value")
                    viewModel.decrease(1.0, stat)
                }
            }
        }
        val timer = Timer()
        timer.scheduleAtFixedRate(lightTimerTask, 0, 10000)

        // Luca.C - 28.10.2021 : initialize fused location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

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
        weatherTimer.scheduleAtFixedRate(weatherTimerTask, 0, 1000 * 60 * 1)

    }

    override fun onResume()
    {
        super.onResume()
        bgVideoView.start()
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
                            Toast.makeText(null, "Failed on getting current location", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            if (location !== null)
            {
                latitude = location.latitude
                longitude = location.longitude
                actualTime = LocalDateTime.now()
                launchExecutor()
            }
            else if (newLocation !== null)
            {
                latitude = newLocation!!.latitude
                longitude = newLocation!!.longitude
                actualTime = LocalDateTime.now()
                launchExecutor()
            }
            else
            {
                Toast.makeText(this, "Failed on getting current location. Please try again later", Toast.LENGTH_SHORT).show()
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
            /* Extracting JSON returns from the API */
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

            //Getting the hours of the actual time to change the background
            val formatter = DateTimeFormatter.ofPattern("HH")
            val formatted = actualTime.format(formatter)

            val isDay = formatted.toInt() in 7..17 // We determine the day time between 7h and 16h

            //Depending on the API results, we will use the correct video
            when (weatherID)
            {
                "Fog"  ->
                {
                    if (isDay) bgVideoView.setVideoPath(resPath + R.raw.fog)
                    else bgVideoView.setVideoPath(resPath + R.raw.fog) //TODO : find fog at night video
                }
                "Rain" ->
                {
                    if (isDay) bgVideoView.setVideoPath(resPath + R.raw.rain)
                    else bgVideoView.setVideoPath(resPath + R.raw.rain) //TODO : find rain at night video
                }
                "Snow" ->
                {
                    if (isDay) bgVideoView.setVideoPath(resPath + R.raw.snow_day)
                    else bgVideoView.setVideoPath(resPath + R.raw.snow_night)
                }
                else   ->
                {
                    if (isDay) bgVideoView.setVideoPath(resPath + R.raw.day)
                    else bgVideoView.setVideoPath(resPath + R.raw.night)
                }
            }
            bgVideoView.start()
        }
        catch (e: Exception)
        {
            Log.e(TAG, "Error: $e")
        }
    }
}
