package woowacourse.shopping.presentation.cart.model

import com.example.domain.model.Product
import com.example.domain.model.Quantity

data class CartUiModel(
    val cartItemId: Int,
    val product: Product,
    val quantity: Quantity,
    val isSelected: Boolean,
)
