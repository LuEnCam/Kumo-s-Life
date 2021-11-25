package ch.hearc.kumoslife.minigame

import android.app.Activity
import android.graphics.Rect
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import ch.hearc.kumoslife.R

class MinigameEnemy(x: Float,y: Float, context: Activity) {
    val speed = 300f // units of distance per second

    val view : ImageView = ImageView(context)

    var x: Float = x
        set(value) { view.x = value; field = value }
    var y: Float = y
        set(value) { view.y = value; field = value }

    companion object {
        val width = 256
        val height = 256
    }

    init {
        view.layoutParams = ConstraintLayout.LayoutParams(width, height)
        this.x = x
        this.y = y
        view.id = ImageView.generateViewId()

        view.setImageResource(R.drawable.ic_launcher_foreground)
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