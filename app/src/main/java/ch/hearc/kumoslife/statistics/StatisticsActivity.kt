package ch.hearc.kumoslife.statistics

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.hearc.kumoslife.KumoFragment
import ch.hearc.kumoslife.R

class StatisticsActivity : AppCompatActivity()
{
	lateinit var adapter: StatisticAdapter

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_statistics)

		val kumoFragment: KumoFragment = KumoFragment.getInstance()

		adapter = StatisticAdapter()
		adapter.setData(kumoFragment.getStats())

		val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
		recyclerView.stopScroll()
		recyclerView.layoutManager = LinearLayoutManager(this)
		recyclerView.adapter = adapter

		findViewById<Button>(R.id.statisticsToMainButton).setOnClickListener() {
			finish()
		}
	}
}