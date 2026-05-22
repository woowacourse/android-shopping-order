package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Product

interface ProductRepository {
    suspend fun requestProductPage(
        page: Int,
        size: Int,
        sort: List<String>? = null,
        category: String?,
    ): ProductPageResult

    suspend fun requestProductDetail(id: Long): Product

    data class ProductPageResult(
        val products: List<Product>,
        val hasNextPage: Boolean,
    )
}
