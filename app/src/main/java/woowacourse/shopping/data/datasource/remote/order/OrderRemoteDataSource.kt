package woowacourse.shopping.data.datasource.remote.order

import woowacourse.shopping.data.remote.retrofit.dto.OrderInfo

interface OrderRemoteDataSource {
    suspend fun order(orderInfo: OrderInfo)
}
