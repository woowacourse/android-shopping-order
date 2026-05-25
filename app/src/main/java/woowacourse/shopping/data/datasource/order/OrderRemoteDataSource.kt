package woowacourse.shopping.data.datasource.order

interface OrderRemoteDataSource {
    suspend fun order(cartItemIds: List<Int>)
}
