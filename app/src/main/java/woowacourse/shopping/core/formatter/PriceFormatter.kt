package woowacourse.shopping.core.formatter

import java.text.DecimalFormat

fun Int.toPriceString(): String {
    val formatter = DecimalFormat("###,###")
    return "${formatter.format(this)}원"
}

fun Int.toDiscountPriceString(): String =
    if (this == 0) {
        toPriceString()
    } else {
        "-${toPriceString()}"
    }
