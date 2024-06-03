package woowacourse.shopping.presentation.utils

import woowacourse.shopping.domain.model.Quantity

class AddCartQuantityBundle(
    val productId: Int,
    val quantity: Quantity,
    val onIncreaseProductQuantity: (productId: Int) -> Unit,
    val onDecreaseProductQuantity: (productId: Int) -> Unit,
)

typealias OnIncreaseProductQuantity = (productId: Int) -> Unit

typealias OnDecreaseProductQuantity = (productId: Int) -> Unit
