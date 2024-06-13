package woowacourse.shopping.data.datasource.remote

import woowacourse.shopping.remote.NetworkResult

interface RemoteOrderDataSource {
    suspend fun requestOrder(cartItemIds: List<Int>): NetworkResult<Unit>
}
