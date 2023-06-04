package woowacourse.shopping.data.remote.request

data class OrderRequestWithoutCoupon(
    val cartItemIds: List<Long>,
)
