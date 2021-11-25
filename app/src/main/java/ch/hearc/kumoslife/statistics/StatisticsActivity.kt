package ch.hearc.kumoslife.statistics

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.hearc.kumoslife.R
import ch.hearc.kumoslife.database.AppDatabase
import ch.hearc.kumoslife.database.StatisticDao
import java.util.*

class StatisticsActivity : AppCompatActivity()
{
	private lateinit var adapter: StatisticAdapter

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_statistics)

		adapter = StatisticAdapter()

		val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
		recyclerView.stopScroll()
		recyclerView.layoutManager = LinearLayoutManager(this)
		recyclerView.adapter = adapter

		// Back to main
		findViewById<Button>(R.id.statisticsToMainButton).setOnClickListener() {
			finish()
		}
	}
}