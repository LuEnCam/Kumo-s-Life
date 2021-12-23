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
    private val TAG: String = StatisticViewModel::class.java.name
    private val statisticsLiveData = MutableLiveData<ArrayList<Statistic>>()
    private val db: AppDatabase = AppDatabase.getInstance()
    private val statisticDao: StatisticDao = db.statisticDao()

    init
    {
        statisticsLiveData.value = ArrayList()

        Executors.newSingleThreadExecutor().execute {
            statisticsLiveData.postValue(statisticDao.getAll() as ArrayList<Statistic>)
        }
    }

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

        // WARNING: has to be initialized before calling this method
        fun getInstance(): StatisticViewModel
        {
            return instance!!
        }
    }

    fun initDataBase()
    {
        Executors.newSingleThreadExecutor().execute {
            Log.i(TAG, "All statistics has been deleted and reinserted in data base")
            statisticDao.deleteAll()
            statisticDao.insert(Statistic(0, "Hunger", 35.0, 8.0))
            statisticDao.insert(Statistic(0, "Thirst", 65.0, 6.9))
            statisticDao.insert(Statistic(0, "Activity", 10.0, 12.5))
            statisticDao.insert(Statistic(0, "Sleep", 15.0, 2.3))
            statisticDao.insert(Statistic(0, "Sickness", 26.0, 1.0))
            statisticsLiveData.postValue(statisticDao.getAll() as ArrayList<Statistic>)
        }
    }

    fun getAllStatistics(): LiveData<ArrayList<Statistic>>
    {
        return statisticsLiveData
    }

    fun getStatisticByName(name: String): Statistic?
    {
        val statistics: ArrayList<Statistic>? = statisticsLiveData.value
        if (statistics != null)
        {
            for (i in 0 until statistics.size)
            {
                if (statistics[i].name == name)
                {
                    return statistics[i]
                }
            }
        }

        return null
    }

    private fun updateStatistic(stat: Statistic)
    {
        // Update in data base
        Executors.newSingleThreadExecutor().execute {
            Log.i("Dao", "Update")
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