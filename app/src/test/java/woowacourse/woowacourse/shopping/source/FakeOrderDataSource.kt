package woowacourse.woowacourse.shopping.source

import woowacourse.shopping.data.source.OrderDataSource

class FakeOrderDataSource(
    private val orderSaved: MutableMap<Long, Int> = mutableMapOf(),
    private val orderService: MutableList<Long> = mutableListOf(),
) : OrderDataSource {
    override suspend fun order2(cartItemIds: List<Long>): Result<Unit> =
        runCatching {
            orderService.addAll(cartItemIds)
        }

    override suspend fun save2(
        cartItemId: Long,
        quantity: Int,
    ): Result<Unit> =
        runCatching {
            orderSaved[cartItemId] = quantity
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
            orderSaved.clear()
        }
}
