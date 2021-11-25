package ch.hearc.kumoslife

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ch.hearc.kumoslife.database.AppDatabase
import ch.hearc.kumoslife.database.Statistic
import java.util.*
import kotlin.collections.ArrayList

class KumoFragment() : Fragment()
{
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
	{
		return inflater.inflate(R.layout.fragment_kumo, container, false)
	}
}