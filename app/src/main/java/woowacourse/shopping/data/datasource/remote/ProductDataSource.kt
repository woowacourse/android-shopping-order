package woowacourse.shopping.data.datasource.remote

import woowacourse.shopping.data.dto.product.ProductContent

interface ProductDataSource {
    suspend fun fetchPagingProducts(
        page: Int?,
        pageSize: Int?,
        category: String?,
    ): List<ProductContent>

    suspend fun fetchProductById(id: Long): ProductContent
}
