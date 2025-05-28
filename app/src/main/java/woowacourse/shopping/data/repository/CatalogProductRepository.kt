package woowacourse.shopping.data.repository

import woowacourse.shopping.product.catalog.ProductUiModel

interface CatalogProductRepository {
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

    fun getProduct(id: Int, callback: (ProductUiModel?) -> Unit)
}
