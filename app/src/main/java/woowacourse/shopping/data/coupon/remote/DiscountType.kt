package woowacourse.shopping.data.coupon.remote

import java.lang.IllegalArgumentException

enum class DiscountType(private val type: String) {
    FIXED("fixed"),
    BUY_X_GET_Y("buyXgetY"),
    FREE_SHIPPING("freeShipping"),
    PERCENTAGE("percentage"),
    ;

    companion object {
        private const val INVALID_DISCOUNT_TYPE_MESSAGE = "올바르지 않은 쿠폰 타입입니다."

        fun from(discountType: String): DiscountType {
            return DiscountType.entries.find { it.type == discountType }
                ?: throw IllegalArgumentException(INVALID_DISCOUNT_TYPE_MESSAGE)
        }
    }
}
