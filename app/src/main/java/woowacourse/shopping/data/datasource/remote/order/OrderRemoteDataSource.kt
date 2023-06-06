package woowacourse.shopping.data.datasource.remote.order

import woowacourse.shopping.data.remote.request.OrderDTO

interface OrderRemoteDataSource {
    fun postOrderWithCoupon(cartItemsIds: List<Long>, couponId: Long, callback: (Result<OrderDTO>) -> Unit)
    fun postOrderWithoutCoupon(cartItemsIds: List<Long>, callback: (Result<OrderDTO>) -> Unit)
}
