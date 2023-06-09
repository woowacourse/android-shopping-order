package woowacourse.shopping.repository

import woowacourse.shopping.domain.cart.CartItem
import java.util.concurrent.CompletableFuture

interface CartItemRepository {

    fun save(cartItem: CartItem): CompletableFuture<Result<CartItem>>

    fun findAll(): CompletableFuture<Result<List<CartItem>>>

    fun findAllByIds(ids: List<Long>): CompletableFuture<Result<List<CartItem>>>

    fun findAllOrderByAddedTime(
        limit: Int,
        offset: Int
    ): CompletableFuture<Result<List<CartItem>>>

    fun findById(id: Long): CompletableFuture<Result<CartItem>>

    fun findByProductId(productId: Long): CompletableFuture<Result<CartItem>>

    fun countAll(): CompletableFuture<Result<Int>>

    fun existByProductId(productId: Long): CompletableFuture<Result<Boolean>>

    fun updateCountById(id: Long, count: Int): CompletableFuture<Result<Unit>>

    fun deleteById(id: Long): CompletableFuture<Result<Unit>>
}
