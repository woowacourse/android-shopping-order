package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.CartItem

interface OrderRepository {
    suspend fun order(cartItems: List<CartItem>): Result<Unit>
}
