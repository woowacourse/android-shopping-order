package woowacourse.shopping.data.datasource.order

import woowacourse.shopping.data.di.ApiResult

interface OrderRemoteDataSource {
    suspend fun postOrderProducts(cartIds: List<Long>): ApiResult<Unit>
}
