package ch.hearc.kumoslife.model.statistics

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface StatisticDao
{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(stat: Statistic)

    @Update
    fun update(stat: Statistic)

    @Query("SELECT * FROM statistic")
    fun getAll(): List<Statistic>

    @Query("DELETE FROM statistic")
    fun deleteAll()
}
