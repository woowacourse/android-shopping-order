package woowacourse.shopping.data.source

import woowacourse.shopping.product.catalog.ProductUiModel

interface CartProductDataSource {
    fun insertCartProduct(
        cartProduct: ProductUiModel,
        callback: (ProductUiModel) -> Unit,
    )

    fun deleteCartProduct(
        cartProduct: ProductUiModel,
        callback: (Boolean) -> Unit
    )

    fun getCartProductsInRange(
        currentPage: Int,
        pageSize: Int,
        callback: (List<ProductUiModel>) -> Unit,
    )

    fun updateProduct(
        cartProduct: ProductUiModel,
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
