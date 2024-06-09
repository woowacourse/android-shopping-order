package woowacourse.shopping.domain

import woowacourse.shopping.data.remote.paging.LoadResult

interface ProductRepository {
    suspend fun getProducts(
        category: String,
        page: Int = 0,
        size: Int = 20,
    ): Result<List<CartProduct>>

    suspend fun getProductsByPaging(
        offset: Int,
        pageSize: Int,
    ): Result<LoadResult.Page<CartProduct>>

    suspend fun getProductById(id: Int): Result<CartProduct?>
}
