package woowacourse.shopping.repository

import woowacourse.shopping.data.remote.paging.LoadResult
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.ProductRepository

class FakeProductRepository : ProductRepository {
    override suspend fun getProducts(
        category: String,
        page: Int,
        size: Int,
    ): Result<List<CartProduct>> {
        TODO("Not yet implemented")
    }

    override suspend fun getProductsByPaging(
        offset: Int,
        pageSize: Int,
    ): Result<LoadResult.Page<CartProduct>> {
        TODO("Not yet implemented")
    }

    override suspend fun getProductById(id: Int): Result<CartProduct?> {
        TODO("Not yet implemented")
    }
}
