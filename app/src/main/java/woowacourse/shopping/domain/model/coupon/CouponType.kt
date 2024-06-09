package woowacourse.shopping.domain.model.coupon

enum class CouponType(val discountType: String) {
    FIXED_DISCOUNT("fixed"),
    BUY_X_GET_Y("buyXgetY"),
    FREE_SHIPPING("freeShipping"),
    PERCENTAGE_DISCOUNT("percentage");

    companion object {
        fun from(discountType: String): CouponType {
            return entries.first { it.discountType == discountType }
        }
    }
}
