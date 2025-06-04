package woowacourse.shopping.data.repository

import woowacourse.shopping.product.catalog.ProductUiModel

interface CartProductRepository {
    fun insertCartProduct(
        cartProduct: ProductUiModel,
        callback: (ProductUiModel) -> Unit,
    )

    fun deleteCartProduct(
        cartItemId: Int,
        callback: (Boolean) -> Unit,
    )

    fun getCartProductsInRange(
        currentPage: Int,
        pageSize: Int,
        callback: (List<ProductUiModel>) -> Unit,
    )

    fun updateProduct(
        cartItemId: Int,
        quantity: Int,
        callback: (Boolean) -> Unit,
    )

    fun getCartItemSize(callback: (Int) -> Unit)

    fun getTotalElements(callback: (Int) -> Unit)

    fun getCartProducts(
        totalElements: Int,
        callback: (List<ProductUiModel>) -> Unit,
    )
}
