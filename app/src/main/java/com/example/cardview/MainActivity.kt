package com.example.cardview

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.example.cardview.customview.CardEditTextView
import com.example.cardview.utils.CardType

class MainActivity : AppCompatActivity(), CardEditTextView.OnCardTypeChangedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val cardEditTextView: CardEditTextView = findViewById(R.id.card_edit_text_view)
        cardEditTextView.setOnCardTypeChangedListener(this)
    }

    override fun onCardTypeChanged(cardType: CardType) {
        Log.e("onCardTypeChanged", cardType.name)
    }

}
