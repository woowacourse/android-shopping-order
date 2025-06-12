package woowacourse.shopping.data.datasource.order

interface OrderRemoteDataSource {
    suspend fun postOrderProducts(cartIds: List<Long>)
}
