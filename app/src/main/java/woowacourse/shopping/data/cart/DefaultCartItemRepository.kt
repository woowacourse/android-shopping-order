package woowacourse.shopping.data.cart

import woowacourse.shopping.data.entity.CartItemEntity.Companion.toDomain
import woowacourse.shopping.domain.cart.CartItem
import woowacourse.shopping.domain.user.User
import woowacourse.shopping.repository.CartItemRepository

class DefaultCartItemRepository(
    private val cartItemDataSource: CartItemDataSource
) : CartItemRepository {
    override fun save(cartItem: CartItem, user: User): Result<CartItem> {
        return cartItemDataSource.save(cartItem.id, user).mapCatching { cartId ->
            cartItem.copy(id = cartId)
        }
    }

    override fun findAll(user: User): Result<List<CartItem>> {
        return cartItemDataSource.findAll(user.token).mapCatching { carts ->
            carts.map { it.toDomain() }
        }
    }

    override fun findAllByIds(ids: List<Long>, user: User): Result<List<CartItem>> {
        return cartItemDataSource.findAll(user.token).mapCatching { carts ->
            carts.filter { it.id in ids }.map { it.toDomain() }
        }
    }

    override fun findAllOrderByAddedTime(
        limit: Int,
        offset: Int,
        user: User
    ): Result<List<CartItem>> {
        return cartItemDataSource.findAll(limit, offset, user.token).mapCatching { carts ->
            carts.map { it.toDomain() }
        }
    }

    override fun findById(id: Long, user: User): Result<CartItem> {
        return cartItemDataSource.findAll(user.token)
            .mapCatching { cartItems -> cartItems.first { it.id == id }.toDomain() }
    }

    override fun findByProductId(productId: Long, user: User): Result<CartItem> {
        return cartItemDataSource.findAll(user.token).mapCatching { cartItems ->
            cartItems.first { it.product.id == productId }.toDomain()
        }
    }

    override fun countAll(user: User): Result<Int> {
        return cartItemDataSource.findAll(user.token).mapCatching {
            it.size
        }
    }

    override fun existByProductId(productId: Long, user: User): Result<Boolean> {
        return cartItemDataSource.findAll(user.token).mapCatching { cartItem ->
            cartItem.any { it.product.id == productId }
        }
    }

    override fun updateCountById(id: Long, count: Int, user: User): Result<Unit> {
        return cartItemDataSource.updateCountById(id, count, user.token)
    }

    override fun deleteById(id: Long, user: User): Result<Unit> {
        return cartItemDataSource.deleteById(id, user.token)
    }
}
