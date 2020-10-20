package com.enping.transformers.ui.edit

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import com.enping.transformers.R
import kotlinx.android.synthetic.main.layout_rating_seekbar.view.*

//TODO, implement custom view onSaveState/onRestoreState
class RatingSeekBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private val root = View.inflate(context, R.layout.layout_rating_seekbar, this)
    private val tvValue: TextView
    private val tvTitle: TextView
    private val skbRating: SeekBar

    private val offset = 1
    private var value = 1
    private val ratingName: String
    var onRatingChangeListener: (Int) -> Unit = { }

    init {
        val attributes = attrs?.let {
            context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.RatingView, defStyleAttr, 0
            )
        }
        ratingName = attributes?.getString(R.styleable.RatingView_ratingName) ?: "unknown"
        attributes?.recycle()

        tvTitle = root.tv_rating_name_rating_seekbar
        skbRating = root.skb_rating_value_rating_seekbar
        tvValue = root.tv_rating_value_rating_seekbar

        tvTitle.text = ratingName.capitalize()
        skbRating.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                value = progress + offset
                update()
                if (fromUser) onRatingChangeListener(value)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        update()
    }

    fun setRating(rating: Int) {
        value = rating
        update()
    }

    private fun update() {
        skbRating.progress = value - offset
        skbRating.jumpDrawablesToCurrentState()
        tvValue.text = value.toString()
    }

}
