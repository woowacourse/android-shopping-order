package woowacourse.shopping.data.server

import woowacourse.shopping.data.order.request.PostOrderRequest

interface OrderRemoteDataSource {
    fun addOrder(order: PostOrderRequest, onSuccess: (Int) -> Unit, onFailure: (String) -> Unit)
}