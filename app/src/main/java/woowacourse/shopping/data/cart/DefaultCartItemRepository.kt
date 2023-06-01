package woowacourse.shopping.data.cart

import woowacourse.shopping.data.entity.CartItemEntity.Companion.toDomain
import woowacourse.shopping.domain.cart.CartItem
import woowacourse.shopping.domain.user.User
import woowacourse.shopping.repository.CartItemRepository

class DefaultCartItemRepository(
    private val cartItemDataSource: CartItemDataSource
) : CartItemRepository {
    override fun save(cartItem: CartItem, user: User, onFinish: (Result<CartItem>) -> Unit) {
        cartItemDataSource.save(cartItem.id, user) { result ->
            onFinish(result.mapCatching { cartId -> cartItem.copy(id = cartId) })
        }
    }

    override fun findAll(user: User, onFinish: (Result<List<CartItem>>) -> Unit) {
        cartItemDataSource.findAll(user.token) { result ->
            onFinish(result.mapCatching { carts -> carts.map { it.toDomain() } })
        }
    }

    override fun findAllByIds(
        ids: List<Long>,
        user: User,
        onFinish: (Result<List<CartItem>>) -> Unit
    ) {
        cartItemDataSource.findAll(user.token) { result ->
            onFinish(
                result.mapCatching { carts ->
                    carts.filter { it.id in ids }.map { it.toDomain() }
                }
            )
        }
    }

    override fun findAllOrderByAddedTime(
        limit: Int,
        offset: Int,
        user: User,
        onFinish: (Result<List<CartItem>>) -> Unit
    ) {
        cartItemDataSource.findAll(limit, offset, user.token) { result ->
            onFinish(result.mapCatching { carts -> carts.map { it.toDomain() } })
        }
    }

    override fun findById(id: Long, user: User, onFinish: (Result<CartItem>) -> Unit) {
        cartItemDataSource.findAll(user.token) { result ->
            onFinish(result.mapCatching { cartItems -> cartItems.first { it.id == id }.toDomain() })
        }
    }

    override fun findByProductId(
        productId: Long,
        user: User,
        onFinish: (Result<CartItem>) -> Unit
    ) {
        cartItemDataSource.findAll(user.token) { result ->
            onFinish(
                result.mapCatching { cartItems ->
                    cartItems.first { it.product.id == productId }.toDomain()
                }
            )
        }
    }

    override fun countAll(user: User, onFinish: (Result<Int>) -> Unit) {
        cartItemDataSource.findAll(user.token) { result ->
            onFinish(result.mapCatching { it.size })
        }
    }

    override fun existByProductId(
        productId: Long,
        user: User,
        onFinish: (Result<Boolean>) -> Unit
    ) {
        cartItemDataSource.findAll(user.token) { result ->
            onFinish(result.mapCatching { cartItem -> cartItem.any { it.product.id == productId } })
        }
    }

    override fun updateCountById(
        id: Long,
        count: Int,
        user: User,
        onFinish: (Result<Unit>) -> Unit
    ) {
        cartItemDataSource.updateCountById(id, count, user.token, onFinish)
    }

    override fun deleteById(id: Long, user: User, onFinish: (Result<Unit>) -> Unit) {
        cartItemDataSource.deleteById(id, user.token, onFinish)
    }
}
