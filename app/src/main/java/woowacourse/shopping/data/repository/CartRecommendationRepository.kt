package woowacourse.shopping.data.repository

import woowacourse.shopping.product.catalog.ProductUiModel

interface CartRecommendationRepository {
    fun getRecommendedProducts(callback: (List<ProductUiModel>) -> Unit)

    fun getSelectedProductsCount(callback: (Int) -> Unit)

    fun insertCartProduct(
        cartProduct: ProductUiModel,
        callback: (ProductUiModel) -> Unit,
    )

    fun updateCartProduct(
        cartProduct: ProductUiModel,
        newCount: Int,
        callback: (Boolean) -> Unit,
    )

    fun deleteCartProduct(
        cartProduct: ProductUiModel,
        callback: (Boolean) -> Unit,
    )
}
