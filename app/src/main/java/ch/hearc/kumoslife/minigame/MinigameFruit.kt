package ch.hearc.kumoslife.minigame

import android.app.Activity
import android.graphics.Rect
import android.util.Log
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import ch.hearc.kumoslife.R
import kotlin.random.Random

class MinigameFruit(x: Float, y: Float, context: Activity) {
    val speed = 300f // units of distance per second

    val view : ImageView = ImageView(context)

    var x: Float = x
        set(value) { view.x = value; field = value }
    var y: Float = y
        set(value) { view.y = value; field = value }

    companion object {
        const val width = 96
        const val height = 96
    }

    init {
        view.layoutParams = ConstraintLayout.LayoutParams(width, height)
        this.x = x
        this.y = y
        view.id = ImageView.generateViewId()

        Log.i("MinigameFruit", "New fruit view with id: ${view.id}")

        // all possible fruit variants as a ressource ID
        val fruits = arrayOf(
            R.drawable.apple,
            R.drawable.apricot,
            R.drawable.banana,
            R.drawable.cherry,
            R.drawable.grapes,
            R.drawable.pear,
            R.drawable.pineapple,
            R.drawable.strawberry
        )

        view.setImageResource(fruits[Random.nextInt(fruits.size)])
    }

    // get rekt, returns the fruit's bounding box to be used for collision detection
    fun getRect(): Rect
    {
        return Rect(
            view.x.toInt(),
            view.y.toInt(),
            (view.x + view.width).toInt(),
            (view.y + view.height).toInt()
        )
    }
}