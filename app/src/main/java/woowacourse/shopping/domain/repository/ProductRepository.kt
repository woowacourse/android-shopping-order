package woowacourse.shopping.domain.repository

import woowacourse.shopping.data.model.ProductResponse

interface ProductRepository {
    fun getProducts(
        category: String?,
        page: Int,
        size: Int,
        sort: String,
    ): Result<ProductResponse>

    fun getProductById(id: Int): Result<woowacourse.shopping.data.model.Product>
}
