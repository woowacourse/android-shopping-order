package woowacourse.shopping.presentation.model

import woowacourse.shopping.domain.model.Cart
import java.io.Serializable

class CartsWrapper(val carts: List<Cart>) : Serializable
