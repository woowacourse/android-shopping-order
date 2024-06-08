package woowacourse.shopping.domain.model

enum class DiscountType(private val discountType: String) {
    FIXED("fixed"),
    BUY_X_GET_Y("buyXgetY"),
    FREE_SHIPPING("freeShipping"),
    PERCENTAGE("percentage"),
    INVALID(""),
    ;

    companion object {
        fun convertStringToType(inputDiscountType: String): DiscountType {
            return entries.find { it.discountType == inputDiscountType } ?: INVALID
        }
    }
}
