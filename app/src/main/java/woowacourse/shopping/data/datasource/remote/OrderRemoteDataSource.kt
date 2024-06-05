package woowacourse.shopping.data.datasource.remote

interface OrderRemoteDataSource {
    suspend fun postOrderByIds(cartItemIds: List<Int>)
}
