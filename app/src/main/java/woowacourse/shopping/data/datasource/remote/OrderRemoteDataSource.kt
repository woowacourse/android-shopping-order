package woowacourse.shopping.data.datasource.remote

interface OrderRemoteDataSource {
    suspend fun order(cartIds: List<Long>): Result<Unit>
}
