package woowacourse.shopping.presentation.utils

import com.example.domain.model.Quantity

class AddCartQuantityBundle(
    val productId: Int,
    val quantity: com.example.domain.model.Quantity,
    val onIncreaseProductQuantity: (productId: Int) -> Unit,
    val onDecreaseProductQuantity: (productId: Int) -> Unit,
)

typealias OnIncreaseProductQuantity = (productId: Int) -> Unit

typealias OnDecreaseProductQuantity = (productId: Int) -> Unit
