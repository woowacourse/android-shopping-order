package woowacourse.shopping.repository

import woowacourse.shopping.data.remote.paging.LoadResult
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.repository.ProductRepository

class FakeProductRepository : ProductRepository {
    override suspend fun getAllByPagingAndCategory(
        category: String,
        page: Int,
        size: Int,
    ): Result<List<CartProduct>> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllByPaging(
        offset: Int,
        pageSize: Int,
    ): Result<LoadResult.Page<CartProduct>> {
        TODO("Not yet implemented")
    }

    override suspend fun getById(id: Int): Result<CartProduct?> {
        TODO("Not yet implemented")
    }
}
