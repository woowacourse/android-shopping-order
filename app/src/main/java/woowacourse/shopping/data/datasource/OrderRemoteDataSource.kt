package woowacourse.shopping.data.datasource

interface OrderRemoteDataSource {
    fun addOrder(cartItemIds: List<String>): Result<Unit>
}
