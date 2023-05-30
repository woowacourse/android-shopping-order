package woowacourse.shopping.data.order

import woowacourse.shopping.domain.cart.Cart
import woowacourse.shopping.domain.cart.CartItem
import woowacourse.shopping.domain.order.DiscountPolicy
import woowacourse.shopping.domain.order.Order
import woowacourse.shopping.domain.order.Payment
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.repository.OrderRepository
import java.time.LocalDateTime

class OrderRepositoryImpl(private val orderDataSource: OrderDataSource) : OrderRepository {
    override fun save(cart: Cart, onFinish: (Long) -> Unit) {
        onFinish(0)
    }

    override fun findById(id: Long, onFinish: (Order) -> Unit) {
        onFinish(
            Order(
                0,
                Cart(
                    listOf(
                        CartItem(
                            0, 0,
                            Product(
                                0,
                                "asdf",
                                0,
                                "https://i.namu.wiki/i/3SNrFXL0Nqv2oS7urTbIaBEVtyIrHHFbxCjKNdLzSnT2gFsMoTKIV9d7VtmA9rpBKCeeVpor7BYzp9Ui9BtirqU8RsZKM1UmrAJ9sGfnSvZGnwHUbbakXgbW8MmXEdQrNQ6nsI6FJ6LC4U9GL7bcOg.webp"
                            ),
                            LocalDateTime.now()
                        )
                    )
                ),
                Payment(
                    listOf(
                        DiscountPolicy(
                            "", 0, 0
                        )
                    ),
                    0
                )
            )
        )
    }

    override fun findAll(onFinish: (List<Order>) -> Unit) {
        val orders = listOf(
            Order(
                0,
                Cart(
                    listOf(
                        CartItem(
                            0, 0,
                            Product(
                                0,
                                "asdf",
                                0,
                                "https://i.namu.wiki/i/3SNrFXL0Nqv2oS7urTbIaBEVtyIrHHFbxCjKNdLzSnT2gFsMoTKIV9d7VtmA9rpBKCeeVpor7BYzp9Ui9BtirqU8RsZKM1UmrAJ9sGfnSvZGnwHUbbakXgbW8MmXEdQrNQ6nsI6FJ6LC4U9GL7bcOg.webp"
                            ),
                            LocalDateTime.now()
                        )
                    )
                ),
                Payment(
                    listOf(
                        DiscountPolicy(
                            "", 0, 0
                        )
                    ),
                    0
                )
            )
        )
        onFinish(orders)
    }

    override fun findDiscountPolicy(cart: Cart, onFinish: (Payment) -> Unit) {
        onFinish(
            Payment(
                listOf(
                    DiscountPolicy("무슨 할인 1", 5, 10000),
                    DiscountPolicy("무슨 할인 1", 5, 10000),
                    DiscountPolicy("무슨 할인 1", 5, 10000),
                    DiscountPolicy("무슨 할인 1", 5, 10000),
                    DiscountPolicy("무슨 할인 2", 55, 10000)
                ),
                100000
            )
        )
    }
}
