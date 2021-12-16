package ch.hearc.kumoslife.model

import android.content.Context
import androidx.room.AutoMigration
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ch.hearc.kumoslife.model.shop.Food
import ch.hearc.kumoslife.model.shop.ShopDao
import ch.hearc.kumoslife.model.statistics.Statistic
import ch.hearc.kumoslife.model.statistics.StatisticDao
import ch.hearc.kumoslife.views.statistics.StatisticsWorker

@Database(entities = [Statistic::class, Food::class], version = 4)
abstract class AppDatabase : RoomDatabase()
{
    // Static instance: singleton
    companion object
    {
        private val TAG: String = AppDatabase::class.java.name
        private const val DB_NAME: String = "kumo_db"
        private var instance: AppDatabase? = null;

        fun getInstance(context: Context): AppDatabase
        {
            if (instance == null)
            {
                Log.i(TAG, "Data base initialization")
                instance = Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME).fallbackToDestructiveMigration().build()
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