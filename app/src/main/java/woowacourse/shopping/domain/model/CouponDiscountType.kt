package woowacourse.shopping.domain.model

enum class CouponDiscountType(
    val id: Long,
) {
    FIXED_DISCOUNT(1L), // 5000원 할인
    BUY_X_GET_Y_FREE(2L), // 2개 구매시 1개 무료
    FREE_SHIPPING_OVER(3L), // 5만원 이상 무료 배송
    PERCENT_DISCOUNT(4L), // 30% 할인
    ;

    companion object {
        fun from(id: Long): CouponDiscountType =
            entries.find { it.id == id }
                ?: throw IllegalArgumentException("$id")
    }
}
