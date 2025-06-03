package woowacourse.shopping.data.repository

import woowacourse.shopping.product.catalog.ProductUiModel

interface CatalogProductRepository {
    fun getRecommendedProducts(
        category: String,
        page: Int,
        size: Int,
        callback: (List<ProductUiModel>) -> Unit,
    )

    fun getAllProductsSize(callback: (Int) -> Unit)

    fun getCartProductsByIds(
        productIds: List<Int>,
        callback: (List<ProductUiModel>) -> Unit,
    )

    fun getProductsByPage(
        page: Int,
        size: Int,
        callback: (List<ProductUiModel>) -> Unit,
    )

    fun getProduct(
        productId: Int,
        callback: (ProductUiModel) -> Unit,
    )
}
