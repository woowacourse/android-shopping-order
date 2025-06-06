package woowacourse.shopping.data.source

import woowacourse.shopping.product.catalog.ProductUiModel

interface CatalogProductDataSource {
    fun getRecommendedProducts(
        category: String,
        page: Int,
        size: Int,
        callback: (List<ProductUiModel>) -> Unit
    )

    fun getAllProductsSize(callback: (Int) -> Unit)

    fun getCartProductsByUids(
        uids: List<Int>,
        callback: (List<ProductUiModel>) -> Unit,
    )

    fun getProductsByPage(
        page: Int,
        size: Int,
        callback: (List<ProductUiModel>) -> Unit
    )

    fun getProduct(id: Int, callback: (ProductUiModel) -> Unit)
}
