package ch.hearc.kumoslife.model.shop

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food")
data class Food(@PrimaryKey(autoGenerate = true) var id: Int, override var name: String, override var prize: Double, val nutritiveValue: Double, override var imageId: Int) : Item()
{
    override fun info(): String
    {
        return "$nutritiveValue"
    }
}