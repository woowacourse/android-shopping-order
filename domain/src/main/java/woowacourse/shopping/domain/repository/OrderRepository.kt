package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.Cart

interface OrderRepository {
    fun order(cart: Cart, points: Int, onSuccess: (Int) -> Unit, onFailure: (String) -> Unit)
}