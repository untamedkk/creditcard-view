package com.example.cardview.utils

import android.content.Context
import android.text.TextUtils
import com.example.cardview.R
import org.apache.commons.validator.routines.checkdigit.LuhnCheckDigit
import java.util.regex.Pattern

private const val VISA_REGEX: String = "^4[0-9]{12}(?:[0-9]{3})"
private const val AMEX_REGEX: String = "^3[47][0-9]{13}"
private const val MASTERCARD_REGEX: String = "^5[1-5][0-9]{14}?"
private const val DISCOVER_REGEX: String = "^6(?:011|5[0-9]{2})[0-9]{12}"
private const val DINERS_CLUB_REGEX: String = "3(?:0[0-5]|[68][0-9])[0-9]{11}"
private const val JCB_REGEX: String = "^(?:2131|1800|35[0-9]{3})[0-9]{11}"
private const val UNIONPAY_REGEX: String = "^(62[0-9]{14,17})"
private const val MAESTRO_REGEX: String = "^(?:5[0678]\\d\\d|6304|6390|67\\d\\d)\\d{12,15}\$"
private const val SWITCH_REGEX: String = "(4903|4905|4911|4936|6333|6759)[0-9]{12}|(4903|4905|4911|4936|6333|6759)[0-9]{14}|(4903|4905|4911|4936|6333|6759)[0-9]{15}|564182[0-9]{10}|564182[0-9]{12}|564182[0-9]{13}|633110[0-9]{10}|633110[0-9]{12}|633110[0-9]{13}\$"
private const val UNKNOWN_REGEX: String = "\\d{14,19}\$"
private const val VISIBLE_CARD_DIGIT_COUNT: Int = 4

fun getTypeFromCardNumber(cardNumber: Long): CardType {
    val cardType = forCardNumber(cardNumber.toString())!!
    return cardType
}

fun isValidCreditCardNumber(number: String): Boolean = when {
    number.isNullOrEmpty() -> false
    else -> LuhnCheckDigit.LUHN_CHECK_DIGIT.isValid(number.replace("[\\s-]+".toRegex(), ""))
}

fun maskCardNumber(context: Context, cardNumber: String?): String = when (TextUtils.isDigitsOnly(cardNumber)) {
    true -> String.format("%s%s", context.getString(R.string.asterisks_four), cardNumber!!.substring(cardNumber.length - VISIBLE_CARD_DIGIT_COUNT))
    false -> String.format("%s%s", context.getString(R.string.asterisks_four), cardNumber!!.replace(ASTERISK, EMPTY))
}

fun getLastDigitsFromCardNumber(cardNumber: String): String = when {
    cardNumber.isNullOrEmpty() -> EMPTY
    else -> {
        when (TextUtils.isDigitsOnly(cardNumber)) {
            true -> cardNumber.substring(cardNumber.length - VISIBLE_CARD_DIGIT_COUNT, cardNumber.length)
            false -> cardNumber.replace(ASTERISK, EMPTY)
        }
    }
}

fun forCardNumber(cardNumber: String): CardType = when {
    cardNumber.isNullOrEmpty() -> CardType.UNKNOWN
    else -> forCardNumberPattern(cardNumber)
}

private fun forCardNumberPattern(cardNumber: String): CardType {
    if (cardNumber.isNullOrEmpty()) {
        return CardType.UNKNOWN
    }
    CardType.values().forEach { cardType ->
        if (cardType.pattern?.matcher(cardNumber)?.matches()!!) {
            return cardType
        }
    }
    return CardType.UNKNOWN
}

enum class CardType(regex: String, val resource: Int, private val minCardNumberLen: Int, private val maxCardNumberLen: Int, cvvNumberLen: Int) {
    VISA(VISA_REGEX, R.drawable.ic_card_visa, 16, 16, 3),
    MASTERCARD(MASTERCARD_REGEX, R.drawable.ic_card_mastercard, 16, 16, 3),
    DISCOVER(DISCOVER_REGEX, R.drawable.ic_card_discover, 16, 16, 3),
    AMEX(AMEX_REGEX, R.drawable.ic_card_amex, 15, 15, 4),
    DINERS_CLUB(DINERS_CLUB_REGEX, R.drawable.ic_card_diners, 14, 14, 3),
    JCB(JCB_REGEX, R.drawable.ic_card_jcb, 16, 16, 3),
    MAESTRO(MAESTRO_REGEX, R.drawable.ic_card_maestro, 14, 19, 3),
    UNIONPAY(UNIONPAY_REGEX, R.drawable.ic_card_unionpay, 16, 19, 3),
    SWITCH(SWITCH_REGEX, R.drawable.ic_card_switch, 16, 19, 3),
    UNKNOWN(UNKNOWN_REGEX, R.drawable.ic_card_placeholder, 14, 19, 3);

    private val AMEX_SPACE_INDICES: IntArray = intArrayOf(4, 10)
    private val DEFAULT_SPACE_INDICES: IntArray = intArrayOf(4, 8, 12)

    var pattern: Pattern? = null

    init {
        pattern = Pattern.compile(regex)
    }

    fun getIconResource(): Int {
        return resource
    }

    fun getSpaceIndices(): IntArray = when (this) {
        AMEX -> AMEX_SPACE_INDICES
        else -> DEFAULT_SPACE_INDICES
    }

    fun getMaxCardNumberLength(): Int {
        return maxCardNumberLen
    }

    fun validate(cardNumber: String): Boolean {
        if (cardNumber.isNullOrEmpty()) {
            return false
        }
        val cardNumberLength = cardNumber.length
        if (cardNumberLength < minCardNumberLen || cardNumberLength > maxCardNumberLen) {
            return false
        } else if (pattern?.matcher(cardNumber)?.matches()?.not()!!) {
            return false
        }
        return isValidCreditCardNumber(cardNumber)
    }
}