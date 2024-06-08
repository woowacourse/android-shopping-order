package woowacourse.shopping.data.model.dto.coupon

sealed class CouponDto {
    abstract val id: Long
    abstract val code: String
    abstract val description: String
    abstract val expirationDate: String
    abstract val discountType: String
}
