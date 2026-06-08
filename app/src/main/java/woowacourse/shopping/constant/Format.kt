package woowacourse.shopping.constant

object Format {
    fun formatPrice(price: Int): String = "%,d원".format(price)

    fun formatDiscountPrice(price: Int): String =
        if (price == 0) {
            "0원"
        } else {
            "-%,d원".format(price)
        }
}
