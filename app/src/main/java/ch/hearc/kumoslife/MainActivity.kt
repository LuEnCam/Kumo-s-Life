package ch.hearc.kumoslife

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Picture
import android.net.Uri
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import android.content.Intent
import ch.hearc.kumoslife.statistics.StatisticsActivity
import android.widget.VideoView
import android.media.MediaPlayer
import android.media.MediaPlayer.OnPreparedListener
import android.util.Log

class MainActivity : AppCompatActivity()
{
	private val LOCATION_PERMISSION_REQ_CODE = 1000;
	private lateinit var fusedLocationClient: FusedLocationProviderClient
	var latitude: Double = 0.0
	var longitude: Double = 0.0
	private val resPath: String = "android.resource://ch.hearc.kumoslife/"
	private lateinit var bgVideoView: VideoView

	var place = ""
	val API = "9d783bddf8b3eaa718e7d926a18ccb1c"

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		// Luca.C - 28.10.2021 : initialize fused location client
		fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
		findViewById<Button>(R.id.btGetLocation).setOnClickListener {
			getCurrentLocation()
		}

		//
		val backgroundImageView = findViewById<ImageView>(R.id.imageviewGIF)
		val cloudSpriteView 	= findViewById<SpriteView>(R.id.kumo_spriteView)
		val eyesImageView 		= findViewById<ImageView>(R.id.eyes_imageView)
		val mouthImageView 		= findViewById<ImageView>(R.id.mouth_imageView)


		// Adding the drawables (images + gifs) to the ImageViews with Glade
		Glide.with(this).load(R.raw.rain).into(backgroundImageView)
		Glide.with(this).load(R.raw.eye).into(eyesImageView)
		Glide.with(this).load(R.drawable.mouth_happy_white).into(mouthImageView)

		// Background video
		bgVideoView = findViewById(R.id.mainBgVideo)
		bgVideoView.setVideoPath(resPath + R.raw.night)
		bgVideoView.setOnPreparedListener { mp ->
			mp.isLooping = true
			mp.setVolume(0.0F, 0.0F)
		}
		bgVideoView.start()

		// Statistics
		findViewById<Button>(R.id.mainToStatisticsButton).setOnClickListener() {
			intent = Intent(this, StatisticsActivity::class.java)
			startActivity(intent)
		}
	}

	}

	private fun getCurrentLocation() {
		// checking location permission
		if (ActivityCompat.checkSelfPermission(this,
				Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// request permission
			ActivityCompat.requestPermissions(this,
				arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQ_CODE);
			return
		}
		fusedLocationClient.lastLocation
			.addOnSuccessListener { location ->
				// getting the last known or current location
				latitude = location.latitude
				longitude = location.longitude

				weatherTask(this).execute()

			}
			.addOnFailureListener {
				Toast.makeText(this, "Failed on getting current location",
					Toast.LENGTH_SHORT).show()
			}
	}

	inner class weatherTask(_mainActivity : MainActivity) : AsyncTask<String, Void, String>() {

		private val mainActivity = _mainActivity
		override fun doInBackground(vararg params: String?): String? {
			var response:String?
			try{
				response = URL("https://api.openweathermap.org/data/2.5/weather?lat=$latitude&lon=$longitude&appid=$API").readText(
					Charsets.UTF_8
				)
			}catch (e: Exception){
				response = null
			}
			return response
		}

		override fun onPostExecute(result: String?) {
			super.onPostExecute(result)
			try {
				/* Extracting JSON returns from the API */
				val jsonObj = JSONObject(result)
				val main = jsonObj.getJSONObject("main")
				val sys = jsonObj.getJSONObject("sys")
				val wind = jsonObj.getJSONObject("wind")
				val weather = jsonObj.getJSONArray("weather").getJSONObject(0)

				val updatedAt:Long = jsonObj.getLong("dt")
				val updatedAtText = "Updated at: "+ SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(
					Date(updatedAt*1000)
				)
				val temp = main.getString("temp")+"°C"
				val tempMin = "Min Temp: " + main.getString("temp_min")+"°C"
				val tempMax = "Max Temp: " + main.getString("temp_max")+"°C"
				val pressure = main.getString("pressure")
				val humidity = main.getString("humidity")

				val sunrise:Long = sys.getLong("sunrise")
				val sunset:Long = sys.getLong("sunset")
				val windSpeed = wind.getString("speed")
				val weatherDescription = weather.getString("description")
				val weatherID = weather.getString("main")

				findViewById<TextView>(R.id.textView_weater_main).text = "Weather description ${weatherID}"

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

				when (weatherID) {
					"Fog" 	-> Glide.with(mainActivity).load(R.raw.fog).into(findViewById<ImageView>(R.id.imageviewGIF));
					"Rain" 	-> Glide.with(mainActivity).load(R.raw.rain).into(findViewById<ImageView>(R.id.imageviewGIF));
					"Snow" 	-> Glide.with(mainActivity).load(R.raw.snow).into(findViewById<ImageView>(R.id.imageviewGIF));
					else -> { // Note the block
						Glide.with(mainActivity).load(R.raw.snow).into(findViewById<ImageView>(R.id.imageviewGIF));
					}
				}

				val address = jsonObj.getString("name")+", "+sys.getString("country")

			} catch (e: Exception) {
				println("Error !!")
			}

		}

	override fun onResume() {
		super.onResume()
		bgVideoView.start()
	}
