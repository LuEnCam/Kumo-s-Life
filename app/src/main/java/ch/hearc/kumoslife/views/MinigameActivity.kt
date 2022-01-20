package ch.hearc.kumoslife.views

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
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import ch.hearc.kumoslife.R
import ch.hearc.kumoslife.minigame.MinigameFruit
import kotlin.random.Random

class MinigameActivity : AppCompatActivity(), SensorEventListener
{
    private val TAG: String = MinigameActivity::class.java.name

    companion object
    {
        const val MINIGAME_COLLECTED_ID: String = "COLLECTED_FRUITS"
    }

    private var sensorManager: SensorManager? = null
    private var gravitySensor: Sensor? = null

    private val fruits: MutableList<MinigameFruit> = mutableListOf()
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

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        supportActionBar?.hide()

        setContentView(R.layout.activity_minigame)

        // debug
        if (showHitBoxes)
        {
            findViewById<View>(R.id.kumo_fragment_container).setBackgroundColor(Color.MAGENTA)
        }

        Log.i(TAG, "Minigame instance created")
    }

    override fun onResume()
    {
        super.onResume()

        // set the delay to game so we get a high refresh rate and we can also use the
        // sensor changed method to update the game state
        sensorManager?.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_GAME)
        Log.i(TAG, "Game has been resumed")
    }

    override fun onStop()
    {
        super.onStop()
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        sensorManager?.unregisterListener(this, gravitySensor)
        fruits.clear()

        Log.i(TAG, "Minigame instance stopped")
    }

    private fun addFruit(fruit: MinigameFruit)
    {
        if (showHitBoxes)
        {
            fruit.view.setBackgroundColor(Color.GREEN)
        }

        findViewById<ConstraintLayout>(R.id.minigame_constraint).addView(fruit.view)
        fruits.add(fruit)

        Log.i(TAG, "New fruit spawned")
    }

    private fun updateFruits(deltaTime: Float)
    {
        val kumoContainer = findViewById<View>(R.id.kumo_fragment_container)
        val kumoRect = Rect(kumoContainer.left, kumoContainer.top, kumoContainer.right, kumoContainer.bottom)

        val toRemoveFruits = mutableListOf<MinigameFruit>()
        val minigameLayout = findViewById<ConstraintLayout>(R.id.minigame_constraint)

        // collide with player
        for (fruit in fruits)
        {
            fruit.y += (fruit.speed + difficulty * 10f) * deltaTime;
            if (fruit.getRect().intersect(kumoRect))
            {
                score += 100
                difficulty += 1f
                collected += 1
                toRemoveFruits.add(fruit)

                Log.i(TAG, "Fruit caught")
            }
        }

        // collide with the whole screen and remove fruits that are no longer visible
        val minigameRect = Rect(minigameLayout.left, minigameLayout.top, minigameLayout.right, minigameLayout.bottom)
        for (fruit in fruits)
        {
            // if the fruit is not visible anymore
            if (!fruit.getRect().intersect(minigameRect))
            {
                score -= 10
                lives -= 1

                // to avoid removing it twice
                if (!toRemoveFruits.contains(fruit))
                {
                    toRemoveFruits.add(fruit)
                }


                Log.i(TAG, "A fruit exited the screen")
            }
        }

        for (toRemoveFruit in toRemoveFruits)
        {
            minigameLayout.removeView(toRemoveFruit.view)
            fruits.remove(toRemoveFruit)
        }

        if (lives == 0)
        {
            paused = true

            val collectedFood = collected

            Log.i(TAG, "The game has been lost and $collectedFood fruits have been gathered")

            // once we lost open a dialog to alert the user and then return the amount of fruits
            // collected to the calling activity
            AlertDialog.Builder(this).setTitle("You lost").setMessage("You managed to collect $collectedFood coins").setPositiveButton("Sad Times") { _, _ ->
                //Log.i(TAG, "User pressed F")

                val result = Intent()
                result.putExtra(MINIGAME_COLLECTED_ID, collectedFood) // we collect 1 food for every 10 caught in game
                this@MinigameActivity.setResult(Activity.RESULT_OK, result)
                this@MinigameActivity.finish()
            }.show()
        }
    }

    private var lastGeneratedFruitTime: Long = SystemClock.elapsedRealtime()
    private fun generateFruits()
    {
        val deltaTime = (SystemClock.elapsedRealtime() - lastGeneratedFruitTime) / 1000f

        val multiplier: Float = (1.0 / Math.log10(difficulty * 1.5 + 10.0)).toFloat()

        if (deltaTime >= 1f * multiplier)
        {
            lastGeneratedFruitTime = SystemClock.elapsedRealtime()

            val width = findViewById<View>(R.id.minigame_constraint).width
            val maxWidth = width - MinigameFruit.width

            val x = Random.nextFloat() * maxWidth
            addFruit(MinigameFruit(x, 200f, this))
        }
    }

    private fun updateHUD()
    {
        val scoreView = findViewById<TextView>(R.id.minigame_score_textview)
        scoreView.text = "score : $score"

        val lifeView = findViewById<TextView>(R.id.minigame_life_textview)
        lifeView.text = "${lives} : lives"
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

            var bias: Float = 0.5f + (-sensorEvent.values[0] / (1.1f * 9.81f))

            if (bias > 1.0f) bias = 1.0f
            if (bias < 0.0f) bias = 0.0f

            if (!paused)
            {
                constraintSet.setHorizontalBias(R.id.kumo_fragment_container, bias)
                constraintSet.applyTo(findViewById(R.id.minigame_constraint))
            }
        }

        if (!paused)
        {
            updateFruits(deltaTime / 1000f)
            generateFruits()
            updateHUD()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, p1: Int)
    {
        Log.i(TAG, "Sensor accuracy changed.");
    }
}