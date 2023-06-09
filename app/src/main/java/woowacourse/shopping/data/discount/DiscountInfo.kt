package woowacourse.shopping.data.discount

data class DiscountInfo(
    val discountResults: List<DiscountResult>
) {
    fun calculateDiscountedPrice(price: Int): Int {
        return discountResults
            .map(DiscountResult::discountPrice)
            .fold(price) { total, discountPrice ->
                total - discountPrice
            }.takeIf { it > 0 } ?: 0
    }
}

data class DiscountResult(
    val discountPolicy: DiscountPolicy,
    val discountRate: Double,
    val discountPrice: Int
)

enum class DiscountPolicy {
    MEMBER_GRADE, PRICE
}
