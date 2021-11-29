package ch.hearc.kumoslife.views

import android.content.Intent
import android.Manifest
import android.content.pm.PackageManager
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.json.JSONObject
import java.net.URL
import ch.hearc.kumoslife.views.statistics.StatisticsActivity
import android.widget.VideoView
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

class MainActivity : AppCompatActivity()
{
    private val resPath: String = "android.resource://ch.hearc.kumoslife/"

    private val LOCATION_PERMISSION_REQ_CODE = 1000
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var actualTime: LocalDateTime = LocalDateTime.MIN
    var place = ""
    val API = "9d783bddf8b3eaa718e7d926a18ccb1c"    //API key used : allows 60 calls per minute

    private lateinit var bgVideoView: VideoView
    private lateinit var viewModel: StatisticViewModel

    @ExperimentalTime
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //
        val cloudSpriteView = findViewById<SpriteView>(R.id.kumo_spriteView)
        val eyesImageView = findViewById<ImageView>(R.id.eyes_imageView)
        val mouthImageView = findViewById<ImageView>(R.id.mouth_imageView)

        // Adding the drawables (images + gifs) to the ImageViews with Glade
        Glide.with(this).load(R.raw.eye).into(eyesImageView)
        Glide.with(this).load(R.drawable.mouth_happy_white).into(mouthImageView)

        // Background video
        bgVideoView = findViewById(R.id.mainBgVideo)
        bgVideoView.setVideoPath(resPath + R.raw.day)
        bgVideoView.setOnPreparedListener { mp ->
            mp.isLooping = true
            mp.setVolume(0.0F, 0.0F)    // no need of volume
        } // bgVideoView.start()

        // To statistics
        findViewById<Button>(R.id.mainToStatisticsButton).setOnClickListener() {
            intent = Intent(this, StatisticsActivity::class.java)
            startActivity(intent)
        }

        // To shop
        findViewById<Button>(R.id.mainToShopButton).setOnClickListener() {
            intent = Intent(this, ShopActivity::class.java)
            startActivity(intent)
        }

        // Data base initialisation
        val db = AppDatabase.getInstance(applicationContext)
        viewModel = StatisticViewModel.getInstance(this)
        viewModel.setDatabase(db)

        // Data base insertion
        viewModel.insertStatistic(Statistic(0, "Hunger", 0.0, 0.3))
        viewModel.insertStatistic(Statistic(0, "Thirst", 0.0, 1.0))
        viewModel.insertStatistic(Statistic(0, "Activity", 0.0, 2.0))
        viewModel.insertStatistic(Statistic(0, "Sleep", 0.0, 0.1))
        viewModel.insertStatistic(Statistic(0, "Sickness", 80.0, 1.0))

        // Data base update
        val timer = Timer()
        timer.schedule(StatisticsTask(viewModel), 10, 600)

        // Luca.C - 28.10.2021 : initialize fused location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        //Directly fetch localisation at app startup
        getCurrentLocation()
    }

    override fun onResume()
    {
        super.onResume()
        bgVideoView.start()
    }

    // FIXME can't be an inner class ?
    class StatisticsTask(private val viewModel: StatisticViewModel) : TimerTask()
    {
        override fun run()
        {
            viewModel.progressAllStatistics()
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
            latitude = location.latitude
            longitude = location.longitude
            actualTime = LocalDateTime.now()
            weatherTask(this).execute()
        }.addOnFailureListener { Toast.makeText(this, "Failed on getting current location", Toast.LENGTH_SHORT).show() }
    }

    inner class weatherTask(_mainActivity: MainActivity) : AsyncTask<String, Void, String>()
    {
        //private val mainActivity = _mainActivity

        override fun doInBackground(vararg params: String?): String?
        {
            var response: String?
            try
            {
                response = URL("https://api.openweathermap.org/data/2.5/weather?lat=$latitude&lon=$longitude&appid=$API").readText(Charsets.UTF_8)
            }
            catch (e: Exception)
            {
                response = null
            }
            return response
        }

        override fun onPostExecute(result: String?)
        {
            super.onPostExecute(result)
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

                var isDay = if (formatted.toInt() in 7..17) true else false // We determine the day time between 7h and 16h


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

                //val address = jsonObj.getString("name") + ", " + sys.getString("country")
            }
            catch (e: Exception)
            {
                println("Error !!")
            }
        }
    }
}
