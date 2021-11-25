package ch.hearc.kumoslife.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ch.hearc.kumoslife.database.AppDatabase
import ch.hearc.kumoslife.database.Statistic
import ch.hearc.kumoslife.database.StatisticDao
import java.util.concurrent.Executors

class StatisticViewModel : ViewModel()
{
    private val statisticsLiveData = MutableLiveData<ArrayList<Statistic>>()
    private lateinit var db: AppDatabase
    private lateinit var statisticDao: StatisticDao

    fun setDatabase(db: AppDatabase)
    {
        this.db = db
        statisticDao = db.statisticDao()
        statisticsLiveData.value = ArrayList()
    }

    fun getAllStatistics() : LiveData<ArrayList<Statistic>>
    {
        return statisticsLiveData
    }

    fun insertStatistic(stat: Statistic)
    {
        // Insertion in data base
        Executors.newSingleThreadExecutor().execute {
            statisticDao.insert(stat)
        }

        // Insertion in live data
        statisticsLiveData.value?.add(stat)
    }

    fun updateStatistic(stat: Statistic)
    {
        // Update in data base
        Executors.newSingleThreadExecutor().execute {
            statisticDao.update(stat)
        }

        // Update in live data
        val statistics: MutableList<Statistic>? = statisticsLiveData.value?.toMutableList()
        if (statistics != null)
            for (i in 0 until statistics.size)
                if (statistics[i].id == stat.id)
                    statistics[i] = stat
    }

    fun progressAllStatistics()
    {
        val statistics: List<Statistic>? = statisticsLiveData.value

        if (statistics != null)
            for (stat in statistics)
            {
                stat.value += stat.progress
                if (stat.value > 100)
                    stat.value = 100.0

                updateStatistic(stat)
            }
    }

    fun decrease(decreaseValue: Double, stat: Statistic)
    {
        stat.value -= decreaseValue
        if (stat.value < 0)
            stat.value = 0.0

        updateStatistic(stat)
    }
}