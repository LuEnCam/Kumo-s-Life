package ch.hearc.kumoslife

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class KumoFragment : Fragment()
{
	private var statsList: ArrayList<Statistic> = ArrayList<Statistic>()

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

	fun getStats(): ArrayList<Statistic>
	{
		return statsList
	}

	fun setStat(name: String, value: Int)
	{
		for (statistic in statsList)
		{
			if (name == statistic.name)
			{
				statistic.value = value
				return
			}
		}
		statsList.add(Statistic(name, value))
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
	{
		return inflater.inflate(R.layout.fragment_kumo, container, false)
	}
}