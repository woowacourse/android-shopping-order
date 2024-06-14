package woowacourse.shopping.data.datasource.remote

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.Products

interface ProductRemoteDataSource {
    suspend fun findProductById(id: Long): Product

    suspend fun getPagingProduct(
        page: Int,
        pageSize: Int,
    ): Products
}
