package ch.hearc.kumoslife.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao interface StatisticDao
{
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insert(stat: Statistic)

	@Update
	fun update(stat: Statistic)

	@Query("SELECT * FROM statistic ORDER BY name")
	fun getAll(): LiveData<List<Statistic>>

	@Query("SELECT * FROM statistic WHERE id = :id")
	fun getById(id: Int) : LiveData<Statistic>
}
