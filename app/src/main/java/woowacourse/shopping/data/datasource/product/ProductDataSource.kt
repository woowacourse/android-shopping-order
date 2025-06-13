package woowacourse.shopping.data.datasource.product

import woowacourse.shopping.domain.Page
import woowacourse.shopping.domain.Product

interface ProductDataSource {
    suspend fun fetchProduct(id: Long): Product

    suspend fun fetchPageOfProducts(
        pageIndex: Int,
        pageSize: Int,
    ): Page<Product>
}
