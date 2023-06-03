package woowacourse.shopping.domain

data class DiscountResult(
    val discountPolicy: DiscountPolicy,
    val discountRate: Double,
    val discountPrice: Int
)
