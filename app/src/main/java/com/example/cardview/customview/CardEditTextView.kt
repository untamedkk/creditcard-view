package com.example.cardview.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.text.*
import android.text.style.ReplacementSpan
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import com.example.cardview.R
import com.example.cardview.utils.CardType
import com.example.cardview.utils.forCardNumber

class CardEditTextView : TintedEditText, TextWatcher {

    private lateinit var cardType: CardType
    private lateinit var onCardTypeChangedListener: OnCardTypeChangedListener
    private lateinit var errorTextView: TextView

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    )

    init {
        inputType = InputType.TYPE_CLASS_NUMBER
        setCardIcon(R.drawable.ic_card_placeholder)
        addTextChangedListener(this)
        updateCardType()
    }

    interface OnCardTypeChangedListener {
        fun onCardTypeChanged(cardType: CardType)
    }

    fun setOnCardTypeChangedListener(onCardTypeChangedListener: OnCardTypeChangedListener) {
        this.onCardTypeChangedListener = onCardTypeChangedListener
    }

    fun setErrorTextView(errorTextView: TextView) {
        this.errorTextView = errorTextView
    }

    private fun setCardIcon(icon: Int) = when (text!!.isEmpty()) {
        true -> setCompoundDrawablesWithIntrinsicBounds(
            resources.getDrawable(R.drawable.ic_card_placeholder),
            null,
            null,
            null
        )
        false -> setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0)
    }

    private fun updateCardType() {
        val ct = forCardNumber(text.toString())
        if (!::cardType.isInitialized || cardType != ct) {
            cardType = ct
            setInputFilter(cardType.getMaxCardNumberLength())
            if (::onCardTypeChangedListener.isInitialized) {
                onCardTypeChangedListener.onCardTypeChanged(this.cardType)
            }
        }
    }

    private fun setInputFilter(max: Int) {
        filters = arrayOf(InputFilter.LengthFilter(max))
        invalidate()
    }

    private fun setTextColor() = when (isCardNumberValid()) {
        true -> setTextColor(resources.getColor(R.color.colorPrimary))
        else -> setTextColor(resources.getColor(R.color.colorAccent))
    }

    private fun setEditTextTint() = when (isCardNumberValid()) {
        true -> this.background.setColorFilter(resources.getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP)
        else -> this.background.setColorFilter(resources.getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP)
    }

    private fun isCardNumberValid(): Boolean = this.cardType.validate(text.toString())

    private fun addSpans(editable: Editable, spaceIndices: IntArray) {
        val length = editable.length
        spaceIndices.forEach { index ->
            if (index <= length) {
                editable.setSpan(SpaceSpan(), (index - 1), index, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
    }

    override fun afterTextChanged(editable: Editable) {
        if (text!!.isNotBlank() && ::errorTextView.isInitialized) {
            errorTextView.visibility = View.INVISIBLE
        }

        val paddingSpans = editable.getSpans(0, editable.length, SpaceSpan::class.java)
        paddingSpans.forEach { span -> editable.removeSpan(span) }

        updateCardType()
        setCardIcon(cardType.getIconResource())
        addSpans(editable, cardType.getSpaceIndices())
        setTextColor()
        setEditTextTint()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(text: CharSequence, start: Int, lengthBefore: Int, lengthAfter: Int) {
    }

    private class SpaceSpan : ReplacementSpan() {

        override fun getSize(paint: Paint, text: CharSequence?, start: Int, end: Int, fm: Paint.FontMetricsInt?): Int {
            val padding = paint.measureText(" ", 0, 1)
            val textSize = paint.measureText(text, start, end)
            return (padding + textSize).toInt()
        }

        override fun draw(
            canvas: Canvas,
            text: CharSequence,
            start: Int,
            end: Int,
            x: Float,
            top: Int,
            y: Int,
            bottom: Int,
            paint: Paint?
        ) {
            canvas.drawText((text.subSequence(start, end).toString() + " "), x, y.toFloat(), paint)
        }
    }

}