package ch.hearc.kumoslife

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import ch.hearc.kumoslife.statistics.StatisticsActivity
import android.widget.VideoView
import android.media.MediaPlayer

import android.media.MediaPlayer.OnPreparedListener
import android.util.Log

class MainActivity : AppCompatActivity()
{
	private val resPath: String = "android.resource://ch.hearc.kumoslife/"
	private lateinit var bgVideoView: VideoView

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

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

	override fun onResume() {
		super.onResume()
		bgVideoView.start()
	}
}