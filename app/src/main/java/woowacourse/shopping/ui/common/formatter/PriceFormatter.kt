package woowacourse.shopping.ui.common.formatter

import android.annotation.SuppressLint

@SuppressLint("DefaultLocale")
fun formatPrice(
    price: Number,
    withSpaceBeforeWon: Boolean = false,
): String {
    val suffix =
        if (withSpaceBeforeWon) {
            " 원"
        } else {
            "원"
        }
    return "%,d".format(price.toLong()) + suffix
}
