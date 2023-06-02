package woowacourse.shopping.repository

import woowacourse.shopping.domain.order.Order
import woowacourse.shopping.domain.order.Payment
import woowacourse.shopping.domain.user.User
import java.util.concurrent.CompletableFuture

interface OrderRepository {
    fun save(cartItemIds: List<Long>, user: User): CompletableFuture<Result<Long>>

    fun findById(id: Long, user: User): CompletableFuture<Result<Order>>

    fun findAll(user: User): CompletableFuture<Result<List<Order>>>

    fun findDiscountPolicy(price: Int, memberGrade: String): CompletableFuture<Result<Payment>>
}
