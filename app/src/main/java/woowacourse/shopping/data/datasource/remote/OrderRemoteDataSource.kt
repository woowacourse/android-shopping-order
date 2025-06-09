package woowacourse.shopping.data.datasource.remote

import woowacourse.shopping.data.model.order.OrderRequest

interface OrderRemoteDataSource {
    suspend fun postOrder(cartProductIds: OrderRequest)
}
