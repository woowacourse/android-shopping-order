package woowacourse.shopping.data.database.cart

import woowacourse.shopping.data.datasource.cart.CartItemDataSource
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
        cartItemDataSource.findAll { cartItems ->
            onFinish(cartItems.sortedBy { it.addedTime })
        }
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

    override fun updateCountById(id: Long, count: Int) {
        cartItemDataSource.updateCountById(id, count)
    }

    override fun deleteById(id: Long) {
        cartItemDataSource.deleteById(id)
    }
}
