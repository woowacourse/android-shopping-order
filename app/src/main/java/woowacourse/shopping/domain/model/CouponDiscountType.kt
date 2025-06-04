package woowacourse.shopping.domain.model

enum class CouponDiscountType(
    val code: String,
) {
    FIXED("fixed"),
    BUY_X_GET_Y("buyXgetY"),
    FREE_SHIPPING("freeShipping"),
    PERCENTAGE("percentage"),
    ;

    companion object {
        fun from(code: String): CouponDiscountType =
            entries.find {
                it.code == code
            } ?: throw IllegalArgumentException("[CouponDiscountType] 알 수 없는 코드")
    }
}
