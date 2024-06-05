package woowacourse.shopping.data.source

interface OrderDataSource {
    fun order(cartItemIds: List<Long>)

    fun save(cartItemId: Long, quantity: Int)

    fun load(): Map<Long, Int>

    fun allQuantity(): Int

    fun claer()
}