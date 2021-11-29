package ch.hearc.kumoslife.modelview

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ch.hearc.kumoslife.model.AppDatabase
import ch.hearc.kumoslife.model.statistics.Statistic
import ch.hearc.kumoslife.model.statistics.StatisticDao
import java.util.concurrent.Executors

class StatisticViewModel : ViewModel()
{
    private val statisticsLiveData = MutableLiveData<ArrayList<Statistic>>()
    private lateinit var db: AppDatabase
    private lateinit var statisticDao: StatisticDao

    // Static instance: singleton
    companion object
    {
        private var instance: StatisticViewModel? = null;

        fun getInstance(activity: AppCompatActivity): StatisticViewModel
        {
            if (instance == null)
            {
                instance = ViewModelProvider(activity).get(StatisticViewModel::class.java)
            }
            return instance as StatisticViewModel
        }
    }

    fun setDatabase(db: AppDatabase)
    {
        this.db = db
        statisticDao = db.statisticDao()
        statisticsLiveData.value = ArrayList()
    }

    fun getAllStatistics(): LiveData<ArrayList<Statistic>>
    {
        return statisticsLiveData
    }

    fun insertStatistic(stat: Statistic)
    {
        // Insertion in data base
        Executors.newSingleThreadExecutor().execute {
            statisticDao.insert(stat)
        }

        statisticsLiveData.value?.add(stat)
        statisticsLiveData.postValue(statisticsLiveData.value)
    }

    fun updateStatistic(stat: Statistic)
    {
        // Update in data base
        Executors.newSingleThreadExecutor().execute {
            statisticDao.update(stat)
        }

        // Update in live data
        val statistics: ArrayList<Statistic>? = statisticsLiveData.value
        if (statistics != null)
        {
            for (i in 0 until statistics.size)
            {
                if (statistics[i].id == stat.id)
                {
                    statistics[i] = stat
                    Log.i("update", "updated")
                }
            }
        }

        statisticsLiveData.postValue(statistics)
    }

    fun progressAllStatistics()
    {
        val statistics: ArrayList<Statistic>? = statisticsLiveData.value

        if (statistics != null)
        {
            for (stat in statistics)
            {
                stat.value += stat.progress
                if (stat.value > 100)
                {
                    stat.value = 100.0
                }

                Executors.newSingleThreadExecutor().execute {
                    statisticDao.update(stat)
                }
            }
        }

        statisticsLiveData.postValue(statistics)
    }

    fun decrease(decreaseValue: Double, stat: Statistic)
    {
        stat.value -= decreaseValue
        if (stat.value < 0)
        {
            stat.value = 0.0
        }

        updateStatistic(stat)
    }
}