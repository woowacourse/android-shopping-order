package woowacourse.shopping.domain.pagination

import woowacourse.shopping.domain.cartsystem.CartPageStatus
import woowacourse.shopping.domain.model.CartProduct

class CartPagination(private val rangeSize: Int, items: List<CartProduct>) :
    NextPagination<CartProduct>, PrevPagination<CartProduct> {
    private var lastIndex = 0
    private val page: Int
        get() = lastIndex / rangeSize
    val status: CartPageStatus
        get() = CartPageStatus(lastIndex > rangeSize, lastIndex < allItems.size, page)
    private val allItems: MutableList<CartProduct> = items.toMutableList()

    override fun fetchNextItems(callback: (List<CartProduct>) -> Unit) {
        if (allItems.size <= lastIndex) return
        val items: List<CartProduct> = if (allItems.size < lastIndex + rangeSize) { // 범위 초과
            allItems.subList(lastIndex, allItems.size)
        } else {
            allItems.subList(lastIndex, lastIndex + rangeSize)
        }
        lastIndex += items.size
        callback(items)
    }

    override fun fetchPrevItems(callback: (List<CartProduct>) -> Unit) {
        if (lastIndex < rangeSize) return
        val items: List<CartProduct>
        if (lastIndex % rangeSize == 0) {
            lastIndex -= rangeSize
        } else {
            lastIndex -= lastIndex % rangeSize
        }
        items = allItems.subList(lastIndex - rangeSize, lastIndex)
        callback(items)
    }

    fun removeItem(cartProduct: CartProduct): CartProduct? { // return next Item
        val nextItem = allItems.getOrNull(lastIndex)
        if (nextItem == null) lastIndex--
        allItems.remove(cartProduct)
        return nextItem
    }

    fun updateItem(cartId: Int, quantity: Int): Boolean {
        val index = allItems.indexOfFirst { it.id == cartId }
        if (index == -1) return false
        allItems[index] = allItems[index].copy(quantity = quantity)
        return true
    }
}
