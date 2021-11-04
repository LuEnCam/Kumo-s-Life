package ch.hearc.kumoslife

import android.content.Context
import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet

class MinigameActivity : AppCompatActivity(), SensorEventListener
{
	private var sensorManager : SensorManager? = null
	private var gravitySensor : Sensor? = null


	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)

		sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
		gravitySensor = sensorManager?.getDefaultSensor(Sensor.TYPE_GRAVITY)

		requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

		setContentView(R.layout.activity_minigame)
	}

	override fun onResume()
	{
		super.onResume()
		sensorManager?.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_GAME)
	}

	override fun onSensorChanged(sensorEvent: SensorEvent?)
	{
		if (sensorEvent != null)
		{
			// the way this works, is that Kumo is already placed in the constraint layout
			// with a bias of 50% at the center, so the only thing we need to do, is to
			// change its bias depending on the gravity
			val constraintSet = ConstraintSet()
			constraintSet.clone(findViewById<ConstraintLayout>(R.id.minigame_constraint))

			var bias : Float = 0.5f + (-sensorEvent.values[0] /  (1.1f * 9.81f))

			if (bias > 1.0f)
				bias = 1.0f
			if (bias < 0.0f)
				bias = 0.0f

			constraintSet.setHorizontalBias(R.id.kumo_fragment_container, bias)
			constraintSet.applyTo(findViewById(R.id.minigame_constraint))
		}

	}

	override fun onAccuracyChanged(sensor: Sensor?, p1: Int) {}

}