package woowacourse.shopping.domain.coupon

sealed interface Coupon {
    val id: Int
    val code: String
    val description: String
    val discountType: String
}
