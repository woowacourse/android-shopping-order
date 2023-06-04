package woowacourse.shopping.data.server

import woowacourse.shopping.data.entity.OrderRequest

interface OrderRemoteDataSource {
    fun addOrder(order: OrderRequest, onSuccess: (Int) -> Unit, onFailure: () -> Unit)
}