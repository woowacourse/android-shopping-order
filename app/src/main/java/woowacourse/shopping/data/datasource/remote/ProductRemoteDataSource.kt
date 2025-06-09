package woowacourse.shopping.data.datasource.remote

import woowacourse.shopping.domain.model.Product

interface ProductRemoteDataSource {
    suspend fun fetchPagingProducts(
        page: Int? = null,
        pageSize: Int? = null,
        category: String? = null,
    ): Result<List<Product>>

    suspend fun isLastPage(page: Int): Result<Boolean>

    suspend fun fetchProductById(id: Long): Result<Product>
}
