package woowacourse.shopping.data.source.remote.products

import woowacourse.shopping.data.model.ProductResponse
import woowacourse.shopping.data.model.ProductsResponse

interface ProductsDataSource {
    fun getProducts(
        page: Int?,
        size: Int?,
        onResult: (Result<ProductsResponse>) -> Unit,
    )

    fun getProductById(
        id: Long,
        onResult: (Result<ProductResponse>) -> Unit,
    )

    fun getProductsByCategory(
        category: String,
        onResult: (Result<ProductsResponse>) -> Unit,
    )
}
