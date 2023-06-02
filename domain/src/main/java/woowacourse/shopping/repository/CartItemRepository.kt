package woowacourse.shopping.repository

import woowacourse.shopping.domain.cart.CartItem
import woowacourse.shopping.domain.user.User
import java.util.concurrent.CompletableFuture

interface CartItemRepository {

    fun save(cartItem: CartItem, user: User): CompletableFuture<Result<CartItem>>

    fun findAll(user: User): CompletableFuture<Result<List<CartItem>>>

    fun findAllByIds(ids: List<Long>, user: User): CompletableFuture<Result<List<CartItem>>>

    fun findAllOrderByAddedTime(
        limit: Int,
        offset: Int,
        user: User
    ): CompletableFuture<Result<List<CartItem>>>

    fun findById(id: Long, user: User): CompletableFuture<Result<CartItem>>

    fun findByProductId(productId: Long, user: User): CompletableFuture<Result<CartItem>>

    fun countAll(user: User): CompletableFuture<Result<Int>>

    fun existByProductId(productId: Long, user: User): CompletableFuture<Result<Boolean>>

    fun updateCountById(id: Long, count: Int, user: User): CompletableFuture<Result<Unit>>

    fun deleteById(id: Long, user: User): CompletableFuture<Result<Unit>>
}
