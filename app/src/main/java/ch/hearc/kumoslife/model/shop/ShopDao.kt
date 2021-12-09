package ch.hearc.kumoslife.model.shop

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.ColumnInfo


@Dao
interface ShopDao
{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(food: Food)

    @Query("SELECT * FROM food")
    fun getAllFood(): List<Food>

    @Query("DELETE FROM food")
    fun deleteAll()

}
