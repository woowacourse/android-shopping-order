package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.CartItemDataSource
import woowacourse.shopping.data.entity.CartItemEntity.Companion.toDomain
import woowacourse.shopping.domain.cart.CartItem
import woowacourse.shopping.domain.user.User
import woowacourse.shopping.repository.CartItemRepository
import java.util.concurrent.CompletableFuture

class DefaultCartItemRepository(
    private val cartItemDataSource: CartItemDataSource
) : CartItemRepository {
    override fun save(cartItem: CartItem, user: User): CompletableFuture<Result<CartItem>> {
        return CompletableFuture.supplyAsync {
            cartItemDataSource.save(cartItem.product.id, user.token).mapCatching { cartId ->
                cartItem.copy(id = cartId)
            }
        }
    }

    override fun findAll(user: User): CompletableFuture<Result<List<CartItem>>> {
        return CompletableFuture.supplyAsync {
            cartItemDataSource.findAll(user.token).mapCatching { carts ->
                carts.map { it.toDomain() }
            }
        }
    }

    override fun findAllByIds(
        ids: List<Long>,
        user: User
    ): CompletableFuture<Result<List<CartItem>>> {
        return CompletableFuture.supplyAsync {
            cartItemDataSource.findAll(user.token).mapCatching { carts ->
                carts.filter { it.id in ids }.map { it.toDomain() }
            }
        }
    }

    override fun findAllOrderByAddedTime(
        limit: Int,
        offset: Int,
        user: User
    ): CompletableFuture<Result<List<CartItem>>> {
        return CompletableFuture.supplyAsync {
            cartItemDataSource.findAll(limit, offset, user.token).mapCatching { carts ->
                carts.map { it.toDomain() }
            }
        }
    }

    override fun findById(id: Long, user: User): CompletableFuture<Result<CartItem>> {
        return CompletableFuture.supplyAsync {
            cartItemDataSource.findAll(user.token)
                .mapCatching { cartItems -> cartItems.first { it.id == id }.toDomain() }
        }
    }

    override fun findByProductId(productId: Long, user: User): CompletableFuture<Result<CartItem>> {
        return CompletableFuture.supplyAsync {
            cartItemDataSource.findAll(user.token).mapCatching { cartItems ->
                cartItems.first { it.product.id == productId }.toDomain()
            }
        }
    }

    override fun countAll(user: User): CompletableFuture<Result<Int>> {
        return CompletableFuture.supplyAsync {
            cartItemDataSource.findAll(user.token).mapCatching {
                it.size
            }
        }
    }

    override fun existByProductId(productId: Long, user: User): CompletableFuture<Result<Boolean>> {
        return CompletableFuture.supplyAsync {
            cartItemDataSource.findAll(user.token).mapCatching { cartItem ->
                cartItem.any { it.product.id == productId }
            }
        }
    }

    override fun updateCountById(
        id: Long,
        count: Int,
        user: User
    ): CompletableFuture<Result<Unit>> {
        return CompletableFuture.supplyAsync {
            cartItemDataSource.updateCountById(id, count, user.token)
        }
    }

    override fun deleteById(id: Long, user: User): CompletableFuture<Result<Unit>> {
        return CompletableFuture.supplyAsync {
            cartItemDataSource.deleteById(id, user.token)
        }
    }
}
