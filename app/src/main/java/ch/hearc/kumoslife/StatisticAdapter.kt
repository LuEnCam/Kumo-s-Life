package ch.hearc.kumoslife

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.collections.ArrayList

class StatisticAdapter() : RecyclerView.Adapter<StatisticAdapter.ViewHolder>()
{
	private var dataList: ArrayList<Statistic> = ArrayList<Statistic>()

	class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
	{
		val progressBar: ProgressBar = itemView.findViewById<ProgressBar>(R.id.progressBarStats)
		val name: TextView = itemView.findViewById<TextView>(R.id.nameTextStats)
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
	{
		val inflater = LayoutInflater.from(parent.context)
		val viewItem = inflater.inflate(R.layout.statistics_element, parent, false)

		return ViewHolder(viewItem)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int)
	{
		val data = dataList[position]
		holder.progressBar.progress = data.value
		holder.name.text = data.name
	}

	override fun getItemCount(): Int
	{
		return dataList.size
	}

	fun setData(dataList: ArrayList<Statistic>)
	{
		this.dataList = dataList
		dataList.sortBy { element -> element.name }
	}
}