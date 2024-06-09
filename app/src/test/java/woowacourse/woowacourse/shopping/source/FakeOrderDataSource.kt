package woowacourse.woowacourse.shopping.source

import woowacourse.shopping.data.source.OrderDataSource

class FakeOrderDataSource(
    private val orderSaved: MutableMap<Long, Int> = mutableMapOf(),
    private val orderService: MutableList<Long> = mutableListOf(),
) : OrderDataSource {
    override fun order(cartItemIds: List<Long>) {
        // TODO: not implemented
    }

    override fun save(
        cartItemId: Long,
        quantity: Int,
    ) {
        TODO("Not yet implemented")
    }

    override fun load(): Map<Long, Int> {
        TODO("Not yet implemented")
    }

    override fun allQuantity(): Int {
        TODO("Not yet implemented")
    }

    override fun claer() {
        TODO("Not yet implemented")
    }

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
