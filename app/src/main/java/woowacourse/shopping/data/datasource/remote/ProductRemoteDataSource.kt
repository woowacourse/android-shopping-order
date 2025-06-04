package woowacourse.shopping.data.datasource.remote

import woowacourse.shopping.domain.model.Product

interface ProductRemoteDataSource {
    suspend fun fetchPagingProducts(
        page: Int?,
        pageSize: Int?,
        category: String?,
    ): Result<List<Product>>

    suspend fun fetchProductById(id: Long): Result<Product>
}
