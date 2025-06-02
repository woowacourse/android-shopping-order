package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.model.response.ProductResponse
import woowacourse.shopping.data.model.response.ProductsResponse

interface ProductDataSource {
    fun fetchProduct(
        id: Long,
        callback: (ProductResponse) -> Unit,
    )

    fun fetchPageOfProducts(
        pageIndex: Int,
        pageSize: Int,
        callback: (ProductsResponse) -> Unit,
    )
}
