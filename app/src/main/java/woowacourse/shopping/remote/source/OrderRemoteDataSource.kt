package woowacourse.shopping.remote.source

import woowacourse.shopping.data.source.OrderDataSource
import woowacourse.shopping.remote.model.request.OrderRequest
import woowacourse.shopping.remote.service.OrderApiService

class OrderRemoteDataSource(private val orderApiService: OrderApiService) : OrderDataSource {
    override suspend fun order2(cartItemIds: List<Long>): Result<Unit> =
        runCatching {
            orderApiService.createOrder2(OrderRequest(cartItemIds))
        }

    override suspend fun save2(
        cartItemId: Long,
        quantity: Int,
    ): Result<Unit> =
        runCatching {
            _orderSaved[cartItemId] = quantity
        }

    override suspend fun load2(): Result<Map<Long, Int>> =
        runCatching {
            orderSaved
        }

    override suspend fun allQuantity2(): Result<Int> =
        runCatching {
            orderSaved.values.sum()
        }

    override suspend fun clear2(): Result<Unit> =
        runCatching {
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
