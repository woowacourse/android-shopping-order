package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.OrderDataSource
import woowacourse.shopping.data.entity.DiscountEntity.Companion.toDomain
import woowacourse.shopping.data.entity.OrderEntity.Companion.toDomain
import woowacourse.shopping.domain.order.Order
import woowacourse.shopping.domain.order.Payment
import woowacourse.shopping.repository.OrderRepository
import woowacourse.shopping.repository.UserRepository
import java.util.concurrent.CompletableFuture

class DefaultOrderRepository(
    private val userRepository: UserRepository,
    private val orderDataSource: OrderDataSource
) : OrderRepository {
    override fun save(cartItemIds: List<Long>): CompletableFuture<Result<Long>> {
        return CompletableFuture.supplyAsync {
            runCatching {
                val user = userRepository.findCurrent().get().getOrThrow()
                orderDataSource.save(cartItemIds, user.token).getOrThrow()
            }
        }
    }

    override fun findById(id: Long): CompletableFuture<Result<Order>> {
        return CompletableFuture.supplyAsync {
            runCatching {
                val user = userRepository.findCurrent().get().getOrThrow()
                val order = orderDataSource.findById(id, user.token).getOrThrow()
                order.toDomain()
            }
        }
    }

    override fun findAll(): CompletableFuture<Result<List<Order>>> {
        return CompletableFuture.supplyAsync {
            runCatching {
                val user = userRepository.findCurrent().get().getOrThrow()
                val orders = orderDataSource.findAll(user.token).getOrThrow()
                orders.map { it.toDomain() }
            }
        }
    }

    override fun findDiscountPolicy(
        price: Int
    ): CompletableFuture<Result<Payment>> {
        return CompletableFuture.supplyAsync {
            runCatching {
                val user = userRepository.findCurrent().get().getOrThrow()
                val discounts =
                    orderDataSource.findDiscountPolicy(price, user.rank.toString()).getOrThrow()
                Payment(discounts.discountInformation.map { it.toDomain() })
            }
        }
    }
}
