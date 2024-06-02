package woowacourse.shopping.data.datasource.remote

interface OrderRemoteDataSource {
    fun postOrderByIds(cartItemsIds: List<Int>): Result<Unit>
}
