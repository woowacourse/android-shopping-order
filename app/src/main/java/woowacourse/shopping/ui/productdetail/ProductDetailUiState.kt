package woowacourse.shopping.ui.productdetail

import woowacourse.shopping.core.designsystem.component.toPriceString
import woowacourse.shopping.ui.uimodel.ProductUiModel

data class ProductDetailUiState(
    val product: ProductUiModel? = null,
    val count: Int = 1,
    val lastViewedProduct: ProductUiModel? = null,
) {
    val formattedTotalPrice: String
        get() = ((product?.price ?: 0) * count).toPriceString()
}
