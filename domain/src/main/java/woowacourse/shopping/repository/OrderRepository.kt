package woowacourse.shopping.repository

import woowacourse.shopping.domain.order.Order
import woowacourse.shopping.domain.order.Payment
import java.util.concurrent.CompletableFuture

interface OrderRepository {
    fun save(cartItemIds: List<Long>): CompletableFuture<Result<Long>>

    fun findById(id: Long): CompletableFuture<Result<Order>>

    fun findAll(): CompletableFuture<Result<List<Order>>>

    fun findDiscountPolicy(price: Int): CompletableFuture<Result<Payment>>
}
