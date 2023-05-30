package woowacourse.shopping.domain.order

data class DiscountPolicy(
    val name: String,
    val discountRate: Int,
    val discountPrice: Int
)
