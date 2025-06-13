package woowacourse.shopping.domain.model.coupon

interface BaseCoupon {
    val id: Int
    val code: String
    val description: String
    val expirationPeriod: ExpirationPeriod
    val minimumAmount: MinimumAmount
}
