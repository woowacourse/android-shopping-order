package woowacourse.shopping.domain.repository

import woowacourse.shopping.data.model.PagedResult
import woowacourse.shopping.domain.model.Product

interface ProductRepository {
    suspend fun getPagedProducts(
        page: Int? = null,
        size: Int? = null,
    ): Result<PagedResult<Product>>
}
