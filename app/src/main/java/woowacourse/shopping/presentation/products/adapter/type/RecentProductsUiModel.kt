package woowacourse.shopping.presentation.products.adapter.type

import woowacourse.shopping.presentation.products.adapter.recent.RecentProductUiModel

data class RecentProductsUiModel(val recentProductUiModels: List<RecentProductUiModel>) : ProductsView {
    override val viewType: ProductsViewType = ProductsViewType.RECENT_PRODUCTS
}
