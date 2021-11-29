package ch.hearc.kumoslife.model.statistics

import androidx.lifecycle.LiveData
import androidx.room.*
import ch.hearc.kumoslife.model.statistics.Statistic

@Dao
interface StatisticDao
{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(stat: Statistic)

    @Update
    fun update(stat: Statistic)

    @Query("SELECT * FROM statistic ORDER BY name")
    fun getAll(): LiveData<List<Statistic>>

    @Query("SELECT * FROM statistic WHERE id = :id")
    fun getById(id: Int): LiveData<Statistic>
}
