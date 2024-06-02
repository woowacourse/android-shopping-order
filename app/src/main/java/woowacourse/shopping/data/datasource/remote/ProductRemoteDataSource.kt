package woowacourse.shopping.data.datasource.remote

import woowacourse.shopping.data.model.remote.ProductDto
import woowacourse.shopping.data.model.remote.ProductsDto

interface ProductRemoteDataSource {
    fun findProductById(id: Long): Result<ProductDto>

    fun getPagingProduct(
        page: Int,
        pageSize: Int,
    ): Result<ProductsDto>
}
