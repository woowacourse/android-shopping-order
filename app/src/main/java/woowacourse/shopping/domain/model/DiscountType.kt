package woowacourse.shopping.domain.model

enum class DiscountType(
    val code: String,
) {
    FIXED("fixed"),
    BUY_X_GET_Y("buyXgetY"),
    FREE_SHIPPING("freeShipping"),
    PERCENTAGE("percentage"),
}
