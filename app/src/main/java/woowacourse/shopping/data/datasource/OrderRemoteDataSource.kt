package woowacourse.shopping.data.datasource

interface OrderRemoteDataSource {
    suspend fun addOrder(cartItemIds: List<String>): Result<Unit>
}
