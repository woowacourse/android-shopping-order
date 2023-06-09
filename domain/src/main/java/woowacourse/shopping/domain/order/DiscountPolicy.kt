package woowacourse.shopping.domain.order

data class DiscountPolicy(
    val name: String,
    val discountRate: Double,
    val discountPrice: Int
)
