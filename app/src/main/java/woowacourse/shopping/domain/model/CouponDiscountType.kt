package woowacourse.shopping.domain.model

enum class CouponDiscountType(
    val discountType: String,
) {
    FIXED_DISCOUNT("fixed"), // 5000원 할인
    BUY_X_GET_Y_FREE("buyXgetY"), // 2개 구매시 1개 무료
    FREE_SHIPPING_OVER("freeShipping"), // 5만원 이상 무료 배송
    PERCENT_DISCOUNT("percentage"), // 30% 할인
    ;

    companion object {
        fun from(discountType: String): CouponDiscountType =
            entries.find { it.discountType == discountType }
                ?: throw IllegalArgumentException(discountType)
    }
}
