package woowacourse.shopping.feature.fake

import woowacourse.shopping.data.repository.cart.CartRepository
import woowacourse.shopping.domain.Cart
import woowacourse.shopping.domain.CartContent
import woowacourse.shopping.domain.Product

class FakeCartRepository(
    initial: List<CartContent> = emptyList(),
) : CartRepository {
    private val contents: MutableList<CartContent> = initial.toMutableList()

    override suspend fun loadCart(): Cart = Cart(contents.toList())

    override suspend fun loadTotalQuantity(): Int = contents.sumOf { it.quantity }

    override suspend fun increase(
        product: Product,
        quantity: Int,
    ) {
        val index = indexOf(product.id)
        if (index == -1) {
            contents.add(CartContent(product, quantity))
        } else {
            val existing = contents[index]
            contents[index] = CartContent(existing.product, existing.quantity + 1)
        }
    }

    override suspend fun decrease(productId: Long) {
        val index = indexOf(productId)
        if (index == -1) return
        val existing = contents[index]
        if (existing.quantity <= 1) {
            contents.removeAt(index)
        } else {
            contents[index] = CartContent(existing.product, existing.quantity - 1)
        }
    }

    override suspend fun pagination(
        page: Int,
        pageSize: Int,
    ): List<CartContent> {
        val startIndex = page * pageSize
        if (startIndex >= contents.size) return emptyList()
        val end = minOf(startIndex + pageSize, contents.size)
        return contents.subList(startIndex, end).toList()
    }

    override suspend fun remove(contentId: Long) {
        val index = indexOf(contentId)
        if (index != -1) contents.removeAt(index)
    }

    override suspend fun setProductQuantity(
        productId: Long,
        quantity: Int,
    ) {
        if (quantity < 1) return
        val index = indexOf(productId)
        if (index == -1) return
        val existing = contents[index]
        contents[index] = CartContent(existing.product, quantity)
    }

    private fun indexOf(productId: Long): Int = contents.indexOfFirst { it.hasProductId(productId) }
}
