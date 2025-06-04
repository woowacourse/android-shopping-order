package woowacourse.shopping.data.repository

import woowacourse.shopping.product.catalog.ProductUiModel

interface CatalogProductRepository {
    fun getRecommendedProducts(
        category: String,
        page: Int,
        size: Int,
        callback: (List<ProductUiModel>) -> Unit,
    )

    fun getAllProductsSize(callback: (Long) -> Unit)

    fun getCartProductsByIds(
        productIds: List<Long>,
        callback: (List<ProductUiModel>) -> Unit,
    )

    fun getProductsByPage(
        page: Int,
        size: Int,
        callback: (List<ProductUiModel>) -> Unit,
    )

    fun getProduct(
        productId: Long,
        onSuccess: (ProductUiModel) -> Unit,
        onFailure: () -> Unit,
    )
}
