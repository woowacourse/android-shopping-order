package woowacourse.shopping.domain.order

data class Payment(
    val discountPolicies: List<DiscountPolicy>
) {
    fun calculateDiscountPrice(): Int = discountPolicies.sumOf { it.discountPrice }
}
