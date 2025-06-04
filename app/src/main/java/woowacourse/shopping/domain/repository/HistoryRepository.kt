package woowacourse.shopping.domain.repository

interface HistoryRepository {
    fun getHistories(onResult: (List<Long>) -> Unit)

    fun saveHistory(
        productId: Long,
        onResult: () -> Unit,
    )
}
