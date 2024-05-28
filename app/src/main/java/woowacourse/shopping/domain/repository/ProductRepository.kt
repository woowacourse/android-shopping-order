package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.Products

interface ProductRepository {
    fun findProductById(id: Long): Result<Product>

    fun getPagingProduct(
        page: Int,
        pageSize: Int,
    ): Result<Products>
}
