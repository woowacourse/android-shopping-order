package woowacourse.shopping.data.datasource.remote.order

import woowacourse.shopping.data.remote.request.OrderDTO

interface OrderDataSource {
    fun postOrderWithCoupon(cartItemsIds: List<Long>, couponId: Long): Result<OrderDTO>
    fun postOrderWithoutCoupon(cartItemsIds: List<Long>): Result<OrderDTO>
}
