package woowacourse.shopping.feature.fake

import woowacourse.shopping.data.repository.cart.CartRepository
import woowacourse.shopping.domain.Cart
import woowacourse.shopping.domain.CartContent
import woowacourse.shopping.domain.Product

class FakeCartRepository(
    initial: List<CartContent> = emptyList(),
    private val productCatalog: List<Product> = emptyList(),
) : CartRepository {

    private val contents: MutableList<CartContent> = initial.toMutableList()
    private var idSeq: Long = initial.size.toLong()

    override suspend fun loadCart(): Cart = Cart(contents.toList())

    override suspend fun loadTotalQuantity(): Int = contents.sumOf { it.quantity }

    override suspend fun pagination(
        page: Int,
        pageSize: Int,
    ): List<CartContent> {
        val start = page * pageSize
        if (start >= contents.size) return emptyList()
        val end = minOf(start + pageSize, contents.size)
        return contents.subList(start, end).toList()
    }

    override suspend fun insert(
        productId: String,
        quantity: Int,
    ) {
        val index = contents.indexOfFirst { it.productId == productId }
        if (index >= 0) {
            contents[index] = contents[index].addQuantity(quantity)
        } else {
            val product = productCatalog.first { it.id == productId }
            contents.add(CartContent(product, quantity, "cart-${idSeq++}"))
        }
    }

    override suspend fun updateQuantity(
        contentId: String,
        quantity: Int,
    ) {
        val index = contents.indexOfFirst { it.id == contentId }
        if (index >= 0) contents[index] = contents[index].changeQuantity(quantity)
    }

    override suspend fun decrease(contentId: String) {
        val index = contents.indexOfFirst { it.id == contentId }
        if (index < 0) return
        val item = contents[index]
        if (item.quantity <= 1) contents.removeAt(index)
        else contents[index] = item.decreaseQuantity(1)
    }

    override suspend fun remove(contentId: String) {
        contents.removeAll { it.id == contentId }
    }
}
