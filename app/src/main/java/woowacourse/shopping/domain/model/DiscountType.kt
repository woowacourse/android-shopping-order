package woowacourse.shopping.domain.model

enum class DiscountType(
    val type: String,
) {
    FIXED("fixed"),
    BUY_X_GET_Y("buyXgetY"),
    FREE_SHIPPING("freeShipping"),
    PERCENTAGE("percentage"),
    ;

    companion object {
        fun from(type: String): DiscountType = entries.find { it.type == type } ?: throw IllegalArgumentException()
    }
}
