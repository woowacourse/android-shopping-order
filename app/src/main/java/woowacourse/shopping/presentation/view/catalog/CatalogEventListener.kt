package woowacourse.shopping.presentation.view.catalog

import woowacourse.shopping.presentation.model.ProductUiModel
import woowacourse.shopping.presentation.view.common.ItemCounterListener

interface CatalogEventListener : ItemCounterListener {
    fun onProductClicked(product: ProductUiModel)

    fun onLoadMoreClicked()
}
