package ch.hearc.kumoslife

import androidx.lifecycle.Observer
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import ch.hearc.kumoslife.statistics.StatisticsActivity
import android.widget.VideoView
import androidx.lifecycle.ViewModelProvider
import ch.hearc.kumoslife.database.AppDatabase
import ch.hearc.kumoslife.database.Statistic
import ch.hearc.kumoslife.database.StatisticDao
import ch.hearc.kumoslife.model.StatisticViewModel
import java.util.*
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity()
{
	private val resPath: String = "android.resource://ch.hearc.kumoslife/"
	private lateinit var bgVideoView: VideoView
	private lateinit var viewModel: StatisticViewModel

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

		// To statistics
		findViewById<Button>(R.id.mainToStatisticsButton).setOnClickListener() {
			intent = Intent(this, StatisticsActivity::class.java)
			startActivity(intent)
		}

		// Data base initialisation
		val db = AppDatabase.getInstance(applicationContext)
		viewModel = ViewModelProvider(this).get(StatisticViewModel::class.java)
		viewModel.setDatabase(db)

		// Data base insertion
		viewModel.insertStatistic(Statistic(0, "Hunger", 0.0, 0.3))
		viewModel.insertStatistic(Statistic(0, "Hunger", 0.0, 0.3))
		viewModel.insertStatistic(Statistic(0, "Thirst", 0.0, 1.0))
		viewModel.insertStatistic(Statistic(0, "Activity", 0.0, 2.0))
		viewModel.insertStatistic(Statistic(0, "Sleep", 0.0, 0.1))
		viewModel.insertStatistic(Statistic(0, "Sickness", 80.0, 1.0))

		viewModel.getAllStatistics().observe(this, Observer {
				statistics -> Log.i("testtttttt", "statistics=$statistics")
		})

		// Data base update
		val timer = Timer()
		timer.schedule(StatisticsTask(viewModel), 10, 60000)
	}

	// FIXME can't be an inner class ?
	class StatisticsTask(private val viewModel: StatisticViewModel) : TimerTask()
	{
		override fun run()
		{
			viewModel.progressAllStatistics()
		}
	}

	override fun onResume()
	{
		super.onResume()
		bgVideoView.start()
	}
}