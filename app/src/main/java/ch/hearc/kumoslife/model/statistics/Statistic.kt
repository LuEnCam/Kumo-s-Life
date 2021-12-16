package ch.hearc.kumoslife.model.statistics

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.lang.StringBuilder

@Entity(tableName = "statistic")
data class Statistic(@PrimaryKey(autoGenerate = true) var id: Int, var name: String, var value: Double, var progress: Double)
{
    override fun toString(): String
    {
        val builder = StringBuilder()
        builder.append("name: ")
        builder.append(name)
        builder.append(", value: ")
        builder.append(value)
        return builder.toString()
    }
}