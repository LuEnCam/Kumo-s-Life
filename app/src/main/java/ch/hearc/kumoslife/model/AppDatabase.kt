package ch.hearc.kumoslife.model

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ch.hearc.kumoslife.model.shop.Food
import ch.hearc.kumoslife.model.shop.ShopDao
import ch.hearc.kumoslife.model.statistics.Statistic
import ch.hearc.kumoslife.model.statistics.StatisticDao

@Database(entities = [Statistic::class, Food::class], version = 4)
abstract class AppDatabase : RoomDatabase()
{
    // Static instance: singleton
    companion object
    {
        private const val DB_NAME: String = "kumo_db"
        private var instance: AppDatabase? = null;

        fun getInstance(context: Context): AppDatabase
        {
            if (instance == null)
            {
                instance = Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME).fallbackToDestructiveMigration().build()
            }
            return instance as AppDatabase
        }
    }

    abstract fun statisticDao(): StatisticDao

    abstract fun shopDao(): ShopDao
}