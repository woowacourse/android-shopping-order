package woowacourse.shopping.ui.catalog.adapter.product

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.ui.catalog.adapter.product.CatalogItemViewType.LOAD_MORE
import woowacourse.shopping.ui.catalog.adapter.product.CatalogItemViewType.PRODUCT

sealed class CatalogItem(
    val viewType: CatalogItemViewType,
) {
    abstract val catalogItemId: Long

    data class ProductItem(
        val value: Product,
    ) : CatalogItem(PRODUCT) {
        override val catalogItemId: Long
            get() = value.productDetail.id
    }

    data object LoadMoreItem : CatalogItem(LOAD_MORE) {
        private const val LOAD_MORE_ITEM_ID = -1L
        override val catalogItemId: Long = LOAD_MORE_ITEM_ID
    }
}
