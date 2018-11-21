package com.example.cardview.customview

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Rect
import android.support.v7.widget.AppCompatEditText
import android.util.AttributeSet
import com.example.cardview.R

open class TintedEditText : AppCompatEditText {

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    )

    init {
        this.background.colorFilter =
                PorterDuffColorFilter(resources.getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP)
    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        if (focused) {
            this.background.setColorFilter(resources.getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP)
        } else {
            this.background.setColorFilter(resources.getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP)
        }
    }

}