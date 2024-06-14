package woowacourse.shopping.domain.model

data class ProductIdsCount(
    val productId: Long,
    val quantity: Int,
) {
    companion object {
        const val INCREASE_VARIATION = 1
        const val DECREASE_VARIATION = -1
    }
}
