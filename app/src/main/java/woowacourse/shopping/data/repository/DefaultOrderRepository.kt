package woowacourse.shopping.data.repository

import woowacourse.shopping.data.entity.DiscountEntity.Companion.toDomain
import woowacourse.shopping.data.entity.OrderEntity.Companion.toDomain
import woowacourse.shopping.data.datasource.OrderDataSource
import woowacourse.shopping.domain.order.Order
import woowacourse.shopping.domain.order.Payment
import woowacourse.shopping.domain.user.User
import woowacourse.shopping.repository.OrderRepository
import java.util.concurrent.CompletableFuture

class DefaultOrderRepository(private val orderDataSource: OrderDataSource) : OrderRepository {
    override fun save(cartItemIds: List<Long>, user: User): CompletableFuture<Result<Long>> {
        return CompletableFuture.supplyAsync {
            orderDataSource.save(cartItemIds, user.token)
        }
    }

    override fun findById(id: Long, user: User): CompletableFuture<Result<Order>> {
        return CompletableFuture.supplyAsync {
            orderDataSource.findById(id, user.token).mapCatching {
                it.toDomain()
            }
        }
    }

    override fun findAll(user: User): CompletableFuture<Result<List<Order>>> {
        return CompletableFuture.supplyAsync {
            orderDataSource.findAll(user.token).mapCatching { orders ->
                orders.map { it.toDomain() }
            }
        }
    }

    override fun findDiscountPolicy(
        price: Int,
        memberGrade: String
    ): CompletableFuture<Result<Payment>> {
        return CompletableFuture.supplyAsync {
            orderDataSource.findDiscountPolicy(price, memberGrade).mapCatching { discounts ->
                Payment(discounts.discountInformation.map { it.toDomain() })
            }
        }
    }
}
