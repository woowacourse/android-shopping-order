
package woowacourse.shopping.domain.model

import java.time.LocalDate

data class BoGoCoupon(
    override val id: Long,
    override val description: String,
    override val expirationDate: LocalDate,
    val buyQuantity: Int,
    val getQuantity: Int,
    ) : Coupon() {
        override fun isSatisfiedPolicy(orders: Orders): Boolean {
            val orderItems = orders.orderItems

            // 구매 수량이 3개 이상인 상품이 있는지 확인
            return orderItems.any { it.quantity > buyQuantity }
        }

        override fun discountAmount(orders: Orders): Int {
            val orderItems = orders.orderItems

            // 구매 수량이 3 개 이상인 카트 아이템들
            val overBuyQuantityCartItems = orderItems.filter { it.quantity > buyQuantity }

            // 구매 수량이 3개 이상인 상품 중 가장 비싼 상품
            val mostExpensiveCartItem = overBuyQuantityCartItems.maxBy { it.product.price }

            return getQuantity * mostExpensiveCartItem.product.price
        }
    }