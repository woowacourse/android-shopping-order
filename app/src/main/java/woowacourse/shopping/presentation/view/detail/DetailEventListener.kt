package woowacourse.shopping.presentation.view.detail

import woowacourse.shopping.presentation.model.ProductUiModel

interface DetailEventListener {
    fun onRecentItemSelected(product: ProductUiModel)
}
