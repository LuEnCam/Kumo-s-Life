package ch.hearc.kumoslife

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import ch.hearc.kumoslife.statistics.StatisticsActivity

class MainActivity : AppCompatActivity()
{
	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		findViewById<Button>(R.id.mainToStatisticsButton).setOnClickListener() {
			intent = Intent(this, StatisticsActivity::class.java)
			startActivity(intent)
		}
	}
}