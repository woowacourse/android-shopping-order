package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.model.response.ProductResponse
import woowacourse.shopping.data.model.response.ProductsResponse

interface ProductDataSource {
    fun fetchProduct(
        id: Long,
        onResult: (ProductResponse) -> Unit,
    )

    fun fetchProducts(
        page: Int,
        size: Int,
        onResult: (ProductsResponse) -> Unit,
        )
}
