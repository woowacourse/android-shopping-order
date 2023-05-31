package woowacourse.shopping.data.cart

import woowacourse.shopping.domain.cart.CartItem
import woowacourse.shopping.domain.user.User
import woowacourse.shopping.repository.CartItemRepository

class DefaultCartItemRepository(
    private val cartItemDataSource: CartItemDataSource
) : CartItemRepository {
    override fun save(cartItem: CartItem, user: User, onFinish: (CartItem) -> Unit) {
        cartItemDataSource.save(cartItem, user, onFinish)
    }

    override fun findAll(user: User, onFinish: (List<CartItem>) -> Unit) {
        cartItemDataSource.findAll(user, onFinish)
    }

    override fun findAllByIds(ids: List<Long>, user: User, onFinish: (List<CartItem>) -> Unit) {
        cartItemDataSource.findAll(user) { cartItems ->
            onFinish(cartItems.filter { it.id in ids })
        }
    }

    override fun findAllOrderByAddedTime(
        limit: Int,
        offset: Int,
        user: User,
        onFinish: (List<CartItem>) -> Unit
    ) {
        cartItemDataSource.findAll(limit, offset, user, onFinish)
    }

    override fun findById(id: Long, user: User, onFinish: (CartItem?) -> Unit) {
        cartItemDataSource.findAll(user) { cartItems ->
            onFinish(cartItems.find { it.id == id })
        }
    }

    override fun findByProductId(productId: Long, user: User, onFinish: (CartItem?) -> Unit) {
        cartItemDataSource.findAll(user) { cartItems ->
            onFinish(cartItems.find { it.product.id == productId })
        }
    }

    override fun countAll(user: User, onFinish: (Int) -> Unit) {
        cartItemDataSource.findAll(user) { cartItems ->
            onFinish(cartItems.size)
        }
    }

    override fun existByProductId(productId: Long, user: User, onFinish: (Boolean) -> Unit) {
        cartItemDataSource.findAll(user) { cartItems ->
            onFinish(cartItems.any { it.product.id == productId })
        }
    }

    override fun updateCountById(id: Long, count: Int, user: User, onFinish: () -> Unit) {
        cartItemDataSource.updateCountById(id, count, user, onFinish)
    }

    override fun deleteById(id: Long, user: User, onFinish: () -> Unit) {
        cartItemDataSource.deleteById(id, user, onFinish)
    }
}
