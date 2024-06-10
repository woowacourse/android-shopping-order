package woowacourse.shopping.presentation.detail

import com.example.domain.model.Product
import com.example.domain.model.Quantity

data class ProductDetailUiModel(
    val product: Product? = null,
    val quantity: Quantity? = null,
    val isSuccess: Boolean = false,
    val isFailure: Boolean = false,
)
