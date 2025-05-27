package woowacourse.shopping.data.repository

import woowacourse.shopping.presentation.product.catalog.ProductUiModel

interface CartItemRepository {
    fun getAllCartItemSize(callback: (Int) -> Unit)

    fun getAllCartItem(callback: (List<ProductUiModel>) -> Unit)

    fun subListCartItems(
        startIndex: Int,
        endIndex: Int,
        callback: (List<ProductUiModel>) -> Unit,
    )

    fun insertCartItem(
        product: ProductUiModel,
        onComplete: () -> Unit,
    )

    fun updateCartItem(
        product: ProductUiModel,
        onComplete: () -> Unit,
    )

    fun deleteCartItemById(
        productId: Long,
        onComplete: () -> Unit,
    )

    fun findCartItem(
        product: ProductUiModel,
        callback: (CartItem?) -> Unit,
    )

    fun updateOrInsertItem(
        product: ProductUiModel,
        callback: () -> Unit,
    )
}
