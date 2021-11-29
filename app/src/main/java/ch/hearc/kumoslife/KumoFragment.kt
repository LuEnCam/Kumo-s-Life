package ch.hearc.kumoslife

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ch.hearc.kumoslife.statistics.Statistic
import java.util.*
import kotlin.collections.ArrayList

class KumoFragment() : Fragment()
{
	private var statsList: ArrayList<Statistic> = ArrayList()

	// Static instance: singleton
	companion object
	{
		private var instance: KumoFragment? = null;
		fun getInstance(): KumoFragment
		{
			if (instance == null)
			{
				instance = KumoFragment()
			}
			return instance as KumoFragment
		}
	}

	class StatisticsTask() : TimerTask()
	{
		override fun run()
		{
			for (stat in KumoFragment.getInstance().statsList)
			{
				stat.progress()
			}
		}
	}

	init
	{
		setStat("Hunger", progress = 0.3)
		setStat("Thirst")
		setStat("Activity", progress = 2.0)
		setStat("Sleep", progress = 0.1)
		setStat("Sickness", 80.0)

		val timer = Timer()
		timer.schedule(StatisticsTask(), 0, 60 * 1000)
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
	{
		return inflater.inflate(R.layout.fragment_kumo, container, false)
	}

	fun getStats(): ArrayList<Statistic>
	{
		return statsList
	}

	fun setStat(name: String, value: Double = 0.0, progress: Double = 1.0)
	{
		statsList.add(Statistic(name, value, progress))
	}
}