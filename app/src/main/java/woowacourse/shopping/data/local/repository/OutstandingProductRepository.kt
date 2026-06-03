package woowacourse.shopping.data.local.repository

interface OutstandingProductRepository {
    suspend fun getAll(): List<Long>

    suspend fun insertAll(cartItemIds: List<Long>)

    suspend fun deleteAll()

    suspend fun replaceAll(cartItemIds: List<Long>)
}
