package woowacourse.shopping.data.cart

import woowacourse.shopping.domain.CartItem
import woowacourse.shopping.repository.CartItemRepository

class CartItemRepositoryImpl(
    private val cartItemDataSource: CartItemDataSource
) : CartItemRepository {
    override fun save(cartItem: CartItem, onFinish: (CartItem) -> Unit) {
        cartItemDataSource.save(cartItem, onFinish)
    }

    override fun findAll(onFinish: (List<CartItem>) -> Unit) {
        cartItemDataSource.findAll(onFinish)
    }

    override fun findAllByIds(ids: List<Long>, onFinish: (List<CartItem>) -> Unit) {
        cartItemDataSource.findAll { cartItems ->
            onFinish(cartItems.filter { it.id in ids })
        }
    }

    override fun findAllOrderByAddedTime(limit: Int, offset: Int, onFinish: (List<CartItem>) -> Unit) {
        cartItemDataSource.findAll(limit, offset, onFinish)
    }

    override fun findById(id: Long, onFinish: (CartItem) -> Unit) {
        cartItemDataSource.findAll { cartItems ->
            onFinish(cartItems.first { it.id == id })
        }
    }

    override fun findByProductId(productId: Long, onFinish: (CartItem?) -> Unit) {
        cartItemDataSource.findAll { cartItems ->
            onFinish(cartItems.find { it.product.id == productId })
        }
    }

    override fun countAll(onFinish: (Int) -> Unit) {
        cartItemDataSource.findAll { cartItems ->
            onFinish(cartItems.size)
        }
    }

    override fun existByProductId(productId: Long, onFinish: (Boolean) -> Unit) {
        cartItemDataSource.findAll { cartItems ->
            onFinish(cartItems.any { it.product.id == productId })
        }
    }

    override fun updateCountById(id: Long, count: Int, onFinish: () -> Unit) {
        cartItemDataSource.updateCountById(id, count, onFinish)
    }

    override fun deleteById(id: Long, onFinish: () -> Unit) {
        cartItemDataSource.deleteById(id, onFinish)
    }
}
