package woowacourse.shopping.data.datasource.remote

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.Products

interface ProductRemoteDataSource {
    fun findProductById(id: Long): Result<Product>

    fun getPagingProduct(
        page: Int,
        pageSize: Int,
    ): Result<Products>
}
