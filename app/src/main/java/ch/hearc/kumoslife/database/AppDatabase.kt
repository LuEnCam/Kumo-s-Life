package ch.hearc.kumoslife.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ch.hearc.kumoslife.MainActivity

@Database(entities = [Statistic::class], version = 1) abstract class AppDatabase : RoomDatabase()
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
				instance = Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME).build()
			}
			return instance as AppDatabase
		}
	}

	abstract fun statisticDao(): StatisticDao
}