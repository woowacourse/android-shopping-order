package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.HomeInfo
import woowacourse.shopping.domain.model.Product

interface ProductRepository {
    suspend fun getProducts(
        category: String?,
        page: Int,
        size: Int,
        sort: String,
    ): Result<HomeInfo>

    suspend fun getProductById(id: Int): Result<Product>
}
