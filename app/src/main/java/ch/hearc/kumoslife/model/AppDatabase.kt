package ch.hearc.kumoslife.model

import android.content.Context
import androidx.room.AutoMigration
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ch.hearc.kumoslife.model.shop.Food
import ch.hearc.kumoslife.model.shop.ShopDao
import ch.hearc.kumoslife.model.statistics.Statistic
import ch.hearc.kumoslife.model.statistics.StatisticDao
import ch.hearc.kumoslife.views.statistics.StatisticsWorker
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import ch.hearc.kumoslife.modelview.ShopViewModel
import ch.hearc.kumoslife.modelview.StatisticViewModel
import java.util.concurrent.TimeUnit


@Database(entities = [Statistic::class, Food::class], version = 5)
abstract class AppDatabase : RoomDatabase()
{
    // Static instance: singleton
    companion object
    {
        private val TAG: String = AppDatabase::class.java.name
        private const val DB_NAME: String = "kumo_db"
        private var instance: AppDatabase? = null
        private lateinit var workManager: WorkManager // unused every time but needed to instantiate

        fun getInstance(activity: AppCompatActivity): AppDatabase
        {
            if (instance == null)
            {
                // TODO Remove this, but here for debug if u want to delete the data base
                // activity.applicationContext.deleteDatabase(DB_NAME)
                // TODO -----------------------------------------------------------------

                val rdc = object : RoomDatabase.Callback()
                {
                    override fun onCreate(db: SupportSQLiteDatabase)
                    {
                        Log.i(TAG, "Data base is initialised")
                        // Data base insertion of fresh new rows
                        val statisticViewModel = StatisticViewModel.getInstance(activity)
                        statisticViewModel.initDataBase()

                        val shopViewModel = ShopViewModel.getInstance(activity)
                        shopViewModel.resetFood()

                        // Data base update every 15 minutes
                        val statisticsWorker = PeriodicWorkRequestBuilder<StatisticsWorker>(15, TimeUnit.MINUTES).build()
                        workManager = WorkManager.getInstance(activity)
                        workManager.enqueueUniquePeriodicWork("statisticsWorker", ExistingPeriodicWorkPolicy.REPLACE, statisticsWorker)
                    }

                    override fun onOpen(db: SupportSQLiteDatabase)
                    {
                        Log.i(TAG, "Data base is opened")
                    }
                }

                Log.i(TAG, "Data base initialization")
                instance = Room.databaseBuilder(activity.applicationContext, AppDatabase::class.java, DB_NAME).fallbackToDestructiveMigration().addCallback(rdc).build()
            }
            return instance as AppDatabase
        }

        // WARNING: has to be initialized before calling this method
        fun getInstance(): AppDatabase
        {
            return instance!!
        }
    }

    abstract fun statisticDao(): StatisticDao

    abstract fun shopDao(): ShopDao
}