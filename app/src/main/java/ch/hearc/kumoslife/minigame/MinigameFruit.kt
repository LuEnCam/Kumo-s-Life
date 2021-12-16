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
        
        val fruits = arrayOf(
            R.drawable.flat_apple,
            R.drawable.flat_apricot,
            R.drawable.flat_banana,
            R.drawable.flat_cherry,
            R.drawable.flat_grape,
            R.drawable.flat_pear,
            R.drawable.flat_pinapple,
            R.drawable.flat_strawberry
        )

        view.setImageResource(fruits[Random.nextInt(fruits.size)])
    }

    // get rekt
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