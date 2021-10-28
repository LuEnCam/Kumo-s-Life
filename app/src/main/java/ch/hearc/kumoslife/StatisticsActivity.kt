package ch.hearc.kumoslife

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class StatisticsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        // TODO Take a list from Kumo's stats directly !
        val dataList: ArrayList<Statistic> = ArrayList<Statistic>()
        dataList.add(Statistic("Hunger", 12))
        dataList.add(Statistic("Thirst", 50))
        dataList.add(Statistic("Activity", 30))
        dataList.add(Statistic("Sleep", 0))
        dataList.add(Statistic("Sickness", 80))

        val adapter = StatisticAdapter()
        adapter.setData(dataList)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }
}