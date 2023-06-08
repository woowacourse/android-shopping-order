package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.CartItemDataSource
import woowacourse.shopping.data.entity.CartItemEntity.Companion.toDomain
import woowacourse.shopping.domain.cart.CartItem
import woowacourse.shopping.repository.CartItemRepository
import woowacourse.shopping.repository.UserRepository
import java.util.concurrent.CompletableFuture

class DefaultCartItemRepository(
    private val userRepository: UserRepository,
    private val cartItemDataSource: CartItemDataSource
) : CartItemRepository {
    override fun save(cartItem: CartItem): CompletableFuture<Result<CartItem>> {
        return CompletableFuture.supplyAsync {
            runCatching {
                val user = userRepository.findCurrent().get().getOrThrow()
                val cartId = cartItemDataSource.save(
                    cartItem.product.id, user.token
                ).getOrThrow()
                cartItem.copy(id = cartId)
            }
        }
    }

    override fun findAll(): CompletableFuture<Result<List<CartItem>>> {
        return CompletableFuture.supplyAsync {
            runCatching {
                val user = userRepository.findCurrent().get().getOrThrow()
                val carts = cartItemDataSource.findAll(user.token).getOrThrow()
                carts.map { it.toDomain() }
            }
        }
    }

    override fun findAllByIds(
        ids: List<Long>
    ): CompletableFuture<Result<List<CartItem>>> {
        return CompletableFuture.supplyAsync {
            runCatching {
                val user = userRepository.findCurrent().get().getOrThrow()
                val carts = cartItemDataSource.findAll(user.token).getOrThrow()
                carts.filter { it.id in ids }.map { it.toDomain() }
            }
        }
    }

    override fun findAllOrderByAddedTime(
        limit: Int,
        offset: Int
    ): CompletableFuture<Result<List<CartItem>>> {
        return CompletableFuture.supplyAsync {
            runCatching {
                val user = userRepository.findCurrent().get().getOrThrow()
                val carts = cartItemDataSource.findAll(limit, offset, user.token).getOrThrow()
                carts.map { it.toDomain() }
            }
        }
    }

    override fun findById(id: Long): CompletableFuture<Result<CartItem>> {
        return CompletableFuture.supplyAsync {
            runCatching {
                val user = userRepository.findCurrent().get().getOrThrow()
                val cartItems = cartItemDataSource.findAll(user.token).getOrThrow()
                cartItems.first { it.id == id }.toDomain()
            }
        }
    }

    override fun findByProductId(productId: Long): CompletableFuture<Result<CartItem>> {
        return CompletableFuture.supplyAsync {
            runCatching {
                val user = userRepository.findCurrent().get().getOrThrow()
                val cartItems = cartItemDataSource.findAll(user.token).getOrThrow()
                cartItems.first { it.product.id == productId }.toDomain()
            }
        }
    }

    override fun countAll(): CompletableFuture<Result<Int>> {
        return CompletableFuture.supplyAsync {
            runCatching {
                val user = userRepository.findCurrent().get().getOrThrow()
                val carts = cartItemDataSource.findAll(user.token).getOrThrow()
                carts.size
            }
        }
    }

    override fun existByProductId(productId: Long): CompletableFuture<Result<Boolean>> {
        return CompletableFuture.supplyAsync {
            runCatching {
                val user = userRepository.findCurrent().get().getOrThrow()
                val cartItem = cartItemDataSource.findAll(user.token).getOrThrow()
                cartItem.any { it.product.id == productId }
            }
        }
    }

    override fun updateCountById(
        id: Long,
        count: Int
    ): CompletableFuture<Result<Unit>> {
        return CompletableFuture.supplyAsync {
            runCatching {
                val user = userRepository.findCurrent().get().getOrThrow()
                cartItemDataSource.updateCountById(id, count, user.token).getOrThrow()
            }
        }
    }

    override fun deleteById(id: Long): CompletableFuture<Result<Unit>> {
        return CompletableFuture.supplyAsync {
            runCatching {
                val user = userRepository.findCurrent().get().getOrThrow()
                cartItemDataSource.deleteById(id, user.token).getOrThrow()
            }
        }
    }
}
