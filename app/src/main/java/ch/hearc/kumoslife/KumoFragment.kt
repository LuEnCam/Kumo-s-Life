package ch.hearc.kumoslife

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide

enum class KumoColor
{
    WHITE, GREEN, GRAY
}

enum class KumosEyes
{
    ANGRY, HAPPY, SAD
}

enum class KumoMouth
{
    HAPPY, SAD
}

class KumoFragment() : Fragment()
{
    //private lateinit var cloudSpriteView : SpriteView
    //private lateinit var eyesImageView : ImageView
    //private lateinit var mouthImageView : ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.fragment_kumo, container, false)
    }

    public fun changeKumosShape(_valueColor: KumoColor, _valueEyes: KumosEyes, _valueMouth: KumoMouth)
    {
        val cloudSpriteView = view?.findViewById<SpriteView>(R.id.kumo_spriteView)!!
        val eyesImageView = view?.findViewById<ImageView>(R.id.eyes_imageView)!!
        val mouthImageView = view?.findViewById<ImageView>(R.id.mouth_imageView)!!

        cloudSpriteView.renderRow = _valueColor.ordinal

        when (_valueEyes)
        {
            KumosEyes.ANGRY ->
            {
                Glide.with(this).load(R.raw.eye_angry).into(eyesImageView)
            }
            KumosEyes.HAPPY ->
            {
                Glide.with(this).load(R.raw.eye).into(eyesImageView)
            }

            KumosEyes.SAD   ->
            {
                Glide.with(this).load(R.raw.eye_sad).into(eyesImageView)
            }
        }

        when (_valueMouth)
        {
            KumoMouth.HAPPY ->
            {
                Glide.with(this).load(R.drawable.mouth_happy).into(mouthImageView)
            }

            KumoMouth.SAD   ->
            {
                Glide.with(this).load(R.drawable.mouth_sad).into(mouthImageView)
            }
        }
    }
}