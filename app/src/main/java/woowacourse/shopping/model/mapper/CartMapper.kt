package woowacourse.shopping.model.mapper

import com.example.domain.cart.Cart
import woowacourse.shopping.model.CartState

fun Cart.toUi(): CartState {
    return CartState(products.map { it.toUi() })
}

fun CartState.toDomain(): Cart {
    return Cart(products.map { it.toDomain() })
}
