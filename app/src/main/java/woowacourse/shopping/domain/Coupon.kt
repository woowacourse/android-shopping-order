package woowacourse.shopping.domain

data class Coupon(
    val id: Long,
    val code: String,
    val description: String,
    val expirationDate: String,
)
