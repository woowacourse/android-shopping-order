package woowacourse.shopping.presentation.cart

import woowacourse.shopping.presentation.product.catalog.ProductUiModel

sealed class CartAdapterItem {
    data class Product(
        val product: ProductUiModel,
    ) : CartAdapterItem()

    data class PaginationButton(
        val hasPrevious: Boolean,
        val hasNext: Boolean,
    ) : CartAdapterItem()
}
