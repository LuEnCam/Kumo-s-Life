package ch.hearc.kumoslife

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class StatisticsActivity : AppCompatActivity()
{
	lateinit var adapter: StatisticAdapter

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_statistics)
		val kumoFragment: KumoFragment = KumoFragment.getInstance()
		kumoFragment.setStat("Hunger", 12)
		kumoFragment.setStat("Thirst", 50)
		kumoFragment.setStat("Activity", 30)
		kumoFragment.setStat("Sleep", 0)
		kumoFragment.setStat("Sickness", 80)

		adapter = StatisticAdapter()
		adapter.setData(kumoFragment.getStats())
		val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
		recyclerView.stopScroll()
		recyclerView.layoutManager = LinearLayoutManager(this)
		recyclerView.adapter = adapter
	}

	override fun onSaveInstanceState(savedInstanceState: Bundle)
	{
		super.onSaveInstanceState(savedInstanceState)		//        savedInstanceState.putStringArrayList("namesList", adapter.)
	}
}