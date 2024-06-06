package woowacourse.shopping.data.remote.paging

import woowacourse.shopping.data.remote.RemoteDataSource
import woowacourse.shopping.data.remote.dto.mapper.toDomain
import woowacourse.shopping.domain.CartProduct

class ProductPagingSource(
    private val remoteDataSource: RemoteDataSource,
) {
    suspend fun load(
        defaultOffset: Int = 0,
        defaultPageSize: Int = 20,
    ): LoadResult<CartProduct> {
        val response = remoteDataSource.getProducts(page = defaultOffset, size = defaultPageSize)

        if (!response.isSuccessful) {
            return LoadResult.Error(LoadErrorType.UNKNOWN)
        }

        val body = response.body() ?: return LoadResult.Error(LoadErrorType.EMPTY_BODY)

        return LoadResult.Page(
            offset = body.number,
            data = body.content.map { it.toDomain() },
        )
    }
}