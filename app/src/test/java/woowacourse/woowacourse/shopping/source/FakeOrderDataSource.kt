package woowacourse.woowacourse.shopping.source

import woowacourse.shopping.data.source.OrderDataSource

class FakeOrderDataSource : OrderDataSource {
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
}
