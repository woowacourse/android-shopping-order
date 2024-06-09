package woowacourse.shopping.domain.model.coupon

sealed interface Coupon {
    val id: Long
    val code: String
    val description: String
    val expirationDate: String
    val discountType: String
}
