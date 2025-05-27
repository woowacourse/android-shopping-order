package woowacourse.shopping.domain.model

data class HistoryProduct(
    val productId: Long,
    val name: String,
    val imageUrl: String,
) {
    companion object {
        val EMPTY_HISTORY_PRODUCT = HistoryProduct(0, "", "")
    }
}
