package woowacourse.shopping.ui.util

import java.text.DecimalFormat

fun formattedPrice(value: Long): String {
    val decimal = DecimalFormat("#,###원")
    return decimal.format(value)
}
