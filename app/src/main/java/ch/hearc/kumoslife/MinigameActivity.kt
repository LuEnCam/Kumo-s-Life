package ch.hearc.kumoslife

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.Rect
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import ch.hearc.kumoslife.minigame.MinigameEnemy
import kotlin.random.Random

class MinigameActivity : AppCompatActivity(), SensorEventListener
{
	companion object
	{
		const val MINIGAME_COLLECTED_ID : String = "COLLECTED_FRUITS"
	}

	private var sensorManager : SensorManager? = null
	private var gravitySensor : Sensor? = null

	private val enemies : MutableList<MinigameEnemy> = mutableListOf()
	private var score = 0
	private var difficulty = 0f
	private var collected = 0
	private var lives = 3
	private var paused = false

	private val showHitBoxes = false

	// the game requires the user to tilt the device, the screen must stay the same orientation
	@SuppressLint("SourceLockedOrientationActivity")
	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)

		sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
		gravitySensor = sensorManager?.getDefaultSensor(Sensor.TYPE_GRAVITY)

		requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

		setContentView(R.layout.activity_minigame)

		// debug
		if (showHitBoxes) {
			findViewById<View>(R.id.kumo_fragment_container).setBackgroundColor(Color.MAGENTA)
		}
	}

	override fun onStop() {
		super.onStop()
		window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
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
		val minigameLayout = findViewById<ConstraintLayout>(R.id.minigame_constraint)

		// collide with player
		for (enemy in enemies)
		{
			enemy.y += (enemy.speed + difficulty * 10f) * deltaTime;
			if (enemy.getRect().intersect(kumoRect))
			{
				score += 100
				difficulty += 1f
				collected += 1
				toRemoveEnemies.add(enemy)
			}
		}

		// collide with the whole screen and remove enemies that are no longer visible
		val minigameRect = Rect(minigameLayout.left, minigameLayout.top, minigameLayout.right, minigameLayout.bottom)
		for (enemy in enemies)
		{
			// if the enemy is not visible anymore
			if (!enemy.getRect().intersect(minigameRect))
			{
				score -= 10
				lives -= 1

				// to avoid removing it twice
				if (!toRemoveEnemies.contains(enemy))
					toRemoveEnemies.add(enemy)
			}
		}

		for (toRemoveEnemy in toRemoveEnemies) {
			minigameLayout.removeView(toRemoveEnemy.view)
			enemies.remove(toRemoveEnemy)
		}

		if (lives == 0)
		{
			paused = true

			val collectedFood = collected / 10

			// once we lost open a dialog to alert the user and then return the amount of fruits
			// collected to the calling activity
			AlertDialog.Builder(this)
				.setTitle("You lost")
				.setMessage("You managed to collect $collectedFood unit(s) of food")
				.setPositiveButton("Press F") {
						_,_ ->
					val result = Intent()
					result.putExtra(MINIGAME_COLLECTED_ID, collectedFood) // we collect 1 food for every 10 caught in game
					setResult(Activity.RESULT_OK, result)
					finish()
				}
				.show()
		}
	}

	private var lastGeneratedEnemyTime: Long = SystemClock.elapsedRealtime()
	private fun generateEnemies()
	{
		val deltaTime = (SystemClock.elapsedRealtime() - lastGeneratedEnemyTime) / 1000f

		val multiplier: Float = (1.0 / Math.log10(difficulty + 10.0)).toFloat()

		if (deltaTime >= 1f * multiplier) {
			lastGeneratedEnemyTime = SystemClock.elapsedRealtime()

			val width = findViewById<View>(R.id.minigame_constraint).width
			val maxWidth = width - MinigameEnemy.width

			val x = Random.nextFloat() * maxWidth
			addEnemy(MinigameEnemy(x, 200f, this))
		}
	}

	private fun updateHUD()
	{
		val scoreView = findViewById<TextView>(R.id.minigame_score_textview)
		scoreView.text = "score: $score | lives: $lives"
	}

	override fun onResume()
	{
		super.onResume()

		// set the delay to game so we get a high refresh rate and we can also use the
		// sensor changed method to update the game state
		sensorManager?.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_GAME)
	}

	private var lastElapsedTime: Long = SystemClock.elapsedRealtime()

	// since this function is called a lot it is also used to update the game state
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

			if (!paused)
			{
				constraintSet.setHorizontalBias(R.id.kumo_fragment_container, bias)
				constraintSet.applyTo(findViewById(R.id.minigame_constraint))
			}
		}

		if (!paused)
		{
			updateEnemies(deltaTime / 1000f)
			generateEnemies()
			updateHUD()
		}
	}

	override fun onAccuracyChanged(sensor: Sensor?, p1: Int) {}

}