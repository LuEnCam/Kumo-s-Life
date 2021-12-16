package ch.hearc.kumoslife.views.statistics

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.hearc.kumoslife.R
import ch.hearc.kumoslife.modelview.StatisticViewModel
import androidx.lifecycle.Observer

class StatisticsActivity : AppCompatActivity()
{
    private lateinit var adapter: StatisticAdapter
    private lateinit var viewModel: StatisticViewModel

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        adapter = StatisticAdapter()

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.stopScroll()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Observer on data base
        viewModel = StatisticViewModel.getInstance(this)
        viewModel.getAllStatistics().observe(this, Observer { statistics ->
            adapter.setData(statistics)
            adapter.notifyDataSetChanged()
        })

        // Back to main
        findViewById<Button>(R.id.statisticsToMainButton).setOnClickListener() {
            finish()
        }
    }
}