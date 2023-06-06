package woowacourse.shopping.data.server

import woowacourse.shopping.data.entity.PayRequest

interface OrderRemoteDataSource {
    fun addOrder(order: PayRequest, onSuccess: (Int) -> Unit, onFailure: (String) -> Unit)
}