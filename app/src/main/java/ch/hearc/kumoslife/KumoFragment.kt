package ch.hearc.kumoslife

import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
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
    ANGRY, HAPPY, SAD, SLEEPING
}

enum class KumoMouth
{
    HAPPY, SAD
}

class KumoFragment : Fragment()
{
    private lateinit var color: KumoColor
    private lateinit var mouth: KumoMouth
    private lateinit var eyes: KumosEyes

    private lateinit var cloudSpriteView: SpriteView
    private lateinit var eyesImageView: ImageView
    private lateinit var mouthImageView: ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.fragment_kumo, container, false)
    }

    fun init()
    {
        cloudSpriteView = view?.findViewById(R.id.kumo_spriteView)!!
        eyesImageView = view?.findViewById(R.id.eyes_imageView)!!
        mouthImageView = view?.findViewById(R.id.mouth_imageView)!!
    }

    fun changeKumosShape(_valueColor: KumoColor, _valueEyes: KumosEyes, _valueMouth: KumoMouth)
    {
        eyes = _valueEyes
        color = _valueColor
        mouth = _valueMouth

        cloudSpriteView.renderRow = _valueColor.ordinal

        when (_valueEyes)
        {
            KumosEyes.ANGRY    ->
            {
                Glide.with(this).load(R.raw.eye_angry).into(eyesImageView)
            }
            KumosEyes.HAPPY    ->
            {
                Glide.with(this).load(R.raw.eye).into(eyesImageView)
            }

            KumosEyes.SAD      ->
            {
                Glide.with(this).load(R.raw.eye_sad).into(eyesImageView)
            }

            KumosEyes.SLEEPING ->
            {
                Glide.with(this).load(R.raw.eye_closed).into(eyesImageView)
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