package woowacourse.woowacourse.shopping.source

import woowacourse.shopping.data.source.OrderDataSource

class FakeOrderDataSource(
    private val orderSaved: MutableMap<Long, Int> = mutableMapOf(),
    private val orderService: MutableList<Long> = mutableListOf(),
) : OrderDataSource {
    override suspend fun order(cartItemIds: List<Long>): Result<Unit> =
        runCatching {
            orderService.addAll(cartItemIds)
        }

    override suspend fun save(
        cartItemId: Long,
        quantity: Int,
    ): Result<Unit> =
        runCatching {
            orderSaved[cartItemId] = quantity
        }

    override suspend fun load(): Result<Map<Long, Int>> =
        runCatching {
            orderSaved
        }

    override suspend fun allQuantity(): Result<Int> =
        runCatching {
            orderSaved.values.sum()
        }

    override suspend fun clear(): Result<Unit> =
        runCatching {
            orderSaved.clear()
        }
}
