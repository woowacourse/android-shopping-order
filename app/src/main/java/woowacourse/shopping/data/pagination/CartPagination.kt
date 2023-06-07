package woowacourse.shopping.data.pagination

import woowacourse.shopping.domain.cartsystem.CartPageStatus
import woowacourse.shopping.domain.model.CartProduct

class CartPagination(private val rangeSize: Int, items: List<CartProduct>) {
    private var lastIndex = 0
    private var page: Int = 0
    val status: CartPageStatus
        get() = CartPageStatus(lastIndex > rangeSize, lastIndex < allItems.size, page)
    private val allItems: MutableList<CartProduct> = items.toMutableList()

    fun fetchNextItems(callback: (List<CartProduct>) -> Unit) {
        if (allItems.size <= lastIndex) return
        val items: List<CartProduct> = if (allItems.size < lastIndex + rangeSize) { // 범위 초과
            allItems.subList(lastIndex, allItems.size)
        } else {
            allItems.subList(lastIndex, lastIndex + rangeSize)
        }
        lastIndex += items.size
        page++
        callback(items)
    }

    fun fetchCurrentItems(callback: (List<CartProduct>) -> Unit) {
        val items: List<CartProduct> = if (allItems.size < lastIndex) { // 범위 초과
            allItems.subList(lastIndex - rangeSize, allItems.size)
        } else {
            allItems.subList(lastIndex - rangeSize, lastIndex)
        }
        callback(items)
    }

    fun fetchPrevItems(callback: (List<CartProduct>) -> Unit) {
        if (lastIndex < rangeSize) return
        val items: List<CartProduct>
        if (lastIndex % rangeSize == 0) {
            lastIndex -= rangeSize
        } else {
            lastIndex -= lastIndex % rangeSize
        }
        items = allItems.subList(lastIndex - rangeSize, lastIndex)
        page--
        callback(items)
    }

    fun removeItem(cartProduct: CartProduct): CartProduct? { // return next Item
        val nextItem = allItems.getOrNull(lastIndex)
        if (nextItem == null) lastIndex--
        allItems.remove(cartProduct)
        if (allItems.isNotEmpty() && allItems.size % rangeSize == 0) page = allItems.size / rangeSize
        return nextItem
    }

    fun updateItem(cartId: Int, quantity: Int): Boolean {
        val index = allItems.indexOfFirst { it.id == cartId }
        if (index == -1) return false
        allItems[index] = allItems[index].copy(quantity = quantity)
        return true
    }
}