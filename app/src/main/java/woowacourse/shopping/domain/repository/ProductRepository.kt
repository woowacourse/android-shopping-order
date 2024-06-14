package woowacourse.shopping.domain.repository

import woowacourse.shopping.data.remote.paging.LoadResult
import woowacourse.shopping.domain.CartProduct

interface ProductRepository {
    suspend fun getAllByPagingAndCategory(
        category: String,
        page: Int = 0,
        size: Int = 20,
    ): Result<List<CartProduct>>

    suspend fun getAllByPaging(
        offset: Int,
        pageSize: Int,
    ): Result<LoadResult.Page<CartProduct>>

    suspend fun getById(id: Int): Result<CartProduct?>
}
