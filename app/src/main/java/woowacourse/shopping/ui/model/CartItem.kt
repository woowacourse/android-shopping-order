package woowacourse.shopping.ui.model

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.remote.model.response.ProductResponse

data class CartItem(
    val id: Long,
    val quantity: Int,
    val product: ProductResponse,
    val checked: Boolean,
)

data class CartItem2(
    val id: Long,
    val quantity: Int,
    val product: Product,
    val checked: Boolean,
)
