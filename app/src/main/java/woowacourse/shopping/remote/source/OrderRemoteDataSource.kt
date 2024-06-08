package woowacourse.shopping.remote.source

import woowacourse.shopping.data.source.OrderDataSource
import woowacourse.shopping.remote.model.request.OrderRequest
import woowacourse.shopping.remote.service.OrderApiService

class OrderRemoteDataSource(private val orderApiService: OrderApiService) : OrderDataSource {
    override fun order(cartItemIds: List<Long>) {
        val orderRequest = OrderRequest(cartItemIds)
        orderApiService.createOrder(orderRequest).execute()
    }

    override fun save(
        cartItemId: Long,
        quantity: Int,
    ) {
        _orderSaved[cartItemId] = quantity
    }

    override fun load(): Map<Long, Int> = orderSaved

    override fun allQuantity(): Int = orderSaved.values.sum()

    override fun claer() {
        _orderSaved.clear()
    }

    companion object {
        private const val TAG = "OrderRemoteDataSource"

        // TODO: 주석 제거
        // id 와 개수 id 는 상품 아이디? (장바구니 아이디가 아니라)
        private val _orderSaved: MutableMap<Long, Int> = mutableMapOf()
        val orderSaved: Map<Long, Int> get() = _orderSaved
    }
}
