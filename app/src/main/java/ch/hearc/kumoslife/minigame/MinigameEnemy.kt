package ch.hearc.kumoslife.minigame

import android.app.Activity
import android.graphics.Rect
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import ch.hearc.kumoslife.R
import kotlin.random.Random

class MinigameEnemy(x: Float,y: Float, context: Activity) {
    val speed = 300f // units of distance per second

    val view : ImageView = ImageView(context)

    var x: Float = x
        set(value) { view.x = value; field = value }
    var y: Float = y
        set(value) { view.y = value; field = value }

    companion object {
        val width = 96
        val height = 96
    }

    init {
        view.layoutParams = ConstraintLayout.LayoutParams(width, height)
        this.x = x
        this.y = y
        view.id = ImageView.generateViewId()

        // fruits from https://www.deviantart.com/anarchisedlute/art/Pixel-Art-32x32-Fruits-Free-Download-784786733
        // free to use for private use
        val fruits = arrayOf(
            R.drawable.ananas,
            R.drawable.apple,
            R.drawable.cherries,
            R.drawable.kiwi,
            R.drawable.orange,
            R.drawable.peach,
            R.drawable.watermelon
        )

        view.setImageResource(fruits[Random.nextInt(fruits.size)])
    }

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