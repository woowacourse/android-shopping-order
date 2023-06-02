package woowacourse.shopping.data.respository.order.source.remote

import woowacouse.shopping.model.order.Order

interface OrderRemoteDataSource {
    fun requestPostData(
        order: Order,
        onFailure: () -> Unit,
        onSuccess: (Long) -> Unit
    )
}
