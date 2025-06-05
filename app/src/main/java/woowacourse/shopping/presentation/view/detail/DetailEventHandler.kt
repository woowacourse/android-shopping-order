package woowacourse.shopping.presentation.view.detail

import woowacourse.shopping.presentation.model.ProductUiModel

interface DetailEventHandler {
    fun onRecentItemSelected(product: ProductUiModel)
}
