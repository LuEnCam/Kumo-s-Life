package ch.hearc.kumoslife

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.Point
import android.graphics.Rect
import android.graphics.RectF
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.getSystemService
import androidx.fragment.app.FragmentContainer
import ch.hearc.kumoslife.minigame.MinigameEnemy
import kotlin.random.Random

class MinigameActivity : AppCompatActivity(), SensorEventListener
{
	private var sensorManager : SensorManager? = null
	private var gravitySensor : Sensor? = null

	private val enemies : MutableList<MinigameEnemy> = mutableListOf()

	private val showHitBoxes = true

	// the game requires the user to tilt the device, the screen must stay the same orientation
	@SuppressLint("SourceLockedOrientationActivity")
	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)

		sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
		gravitySensor = sensorManager?.getDefaultSensor(Sensor.TYPE_GRAVITY)

		requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

		setContentView(R.layout.activity_minigame)

		// debug
		if (showHitBoxes)
			findViewById<View>(R.id.kumo_fragment_container).setBackgroundColor(Color.MAGENTA)
	}

	private fun addEnemy(enemy: MinigameEnemy)
	{
		if (showHitBoxes)
			enemy.view.setBackgroundColor(Color.GREEN)

		findViewById<ConstraintLayout>(R.id.minigame_constraint).addView(enemy.view)
		enemies.add(enemy)
	}

	private fun updateEnemies(deltaTime: Float)
	{
		val kumoContainer = findViewById<View>(R.id.kumo_fragment_container)
		val kumoRect = Rect(kumoContainer.left, kumoContainer.top, kumoContainer.right, kumoContainer.bottom)


		val toRemoveEnemies = mutableListOf<MinigameEnemy>()
		for (enemy in enemies)
		{
			enemy.y += enemy.speed * deltaTime;
			if (kumoRect.intersect(enemy.getRect()))
			{
				findViewById<ConstraintLayout>(R.id.minigame_constraint).removeView(enemy.view)
				toRemoveEnemies.add(enemy)
			}
		}

		for (toRemoveEnemy in toRemoveEnemies)
			enemies.remove(toRemoveEnemy)
	}

	private var lastGeneratedEnemyTime: Long = SystemClock.elapsedRealtime()
	private fun generateEnemies()
	{
		val deltaTime = (SystemClock.elapsedRealtime() - lastGeneratedEnemyTime) / 1000f

		if (deltaTime >= 3f) {
			lastGeneratedEnemyTime = SystemClock.elapsedRealtime()

			val width = findViewById<View>(R.id.minigame_constraint).width
			val maxWidth = width - MinigameEnemy.width

			val x = Random.nextFloat() * maxWidth
			addEnemy(MinigameEnemy(x, 0f, this))
		}
	}

	override fun onResume()
	{
		super.onResume()
		sensorManager?.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_GAME)
	}

	private var lastElapsedTime: Long = SystemClock.elapsedRealtime()

	override fun onSensorChanged(sensorEvent: SensorEvent?)
	{
		var deltaTime = SystemClock.elapsedRealtime() - lastElapsedTime
		lastElapsedTime = SystemClock.elapsedRealtime()

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
		updateEnemies(deltaTime / 1000f)
		generateEnemies()
	}

	override fun onAccuracyChanged(sensor: Sensor?, p1: Int) {}

}