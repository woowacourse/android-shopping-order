package woowacourse.shopping.data.remote

import woowacourse.shopping.data.remote.dto.mapper.toDomain
import woowacourse.shopping.domain.CartProduct

class ProductPagingSource(
    private val remoteDataSource: RemoteDataSource,
) {
    var offset: Int = 0
    val pageSize: Int = 20

    suspend fun load(offsetInput: Int = offset): LoadResult<List<CartProduct>> {
        return try {
            val products = remoteDataSource.getProducts(page = offset, size = pageSize)
            offset++
            LoadResult.Page(
                offset = offsetInput,
                data = products.getOrNull()?.map { it.toDomain() } ?: emptyList(),
            )
        } catch (e: Exception) {
            LoadResult.Error(e.message ?: "Unknown error")
        }
    }
}
