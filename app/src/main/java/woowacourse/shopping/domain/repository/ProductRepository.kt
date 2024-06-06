package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.HomeInfo
import woowacourse.shopping.domain.model.Product

interface ProductRepository {
    fun getProducts(
        category: String?,
        page: Int,
        size: Int,
        sort: String,
    ): Result<HomeInfo>

    fun getProductById(id: Int): Result<Product>
}
