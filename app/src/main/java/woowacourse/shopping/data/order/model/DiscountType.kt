package woowacourse.shopping.data.order.model

enum class DiscountType(val label: String) {
    FIXED("fixed"),
    BUY_X_GET_Y("buyXgetY"),
    FREE_SHIPPING("freeShipping"),
    PERCENTAGE("percentage");

    companion object {
        fun from(label: String): DiscountType? {
            return entries.firstOrNull {
                it.label.uppercase().trim() == label.uppercase().trim()
            }
        }
    }
}