package com.enping.transformers.ui.list

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.enping.transformers.R
import kotlinx.android.synthetic.main.layout_rating_seekbar.view.*
import kotlinx.android.synthetic.main.layout_rating_view.view.*

class RatingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private val root = View.inflate(context, R.layout.layout_rating_view, this)
    private val tvValue: TextView
    private val tvTitle: TextView
    private val ratingName: String

    init {
        val attributes = attrs?.let {
            context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.RatingView, defStyleAttr, 0
            )
        }
        ratingName = attributes?.getString(R.styleable.RatingView_ratingName) ?: "unknown"
        attributes?.recycle()

        tvTitle = root.tv_rating_name_rating_view
        tvValue = root.tv_rating_value_rating_view

        tvTitle.text = ratingName.capitalize()
    }

}
