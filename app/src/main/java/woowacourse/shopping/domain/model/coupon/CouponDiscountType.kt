package woowacourse.shopping.domain.model.coupon

enum class CouponDiscountType(
    private val apiName: String,
) {
    FIXED("fixed"),
    BUY_X_GET_Y("buyXgetY"),
    FREE_SHIPPING("freeShipping"),
    PERCENTAGE("percentage"),
    NOT_FOUND("쿠폰 타입을 찾지 못했습니다."),
    ;

    companion object {
        fun from(name: String): CouponDiscountType = entries.find { it.apiName == name } ?: NOT_FOUND
    }
}
