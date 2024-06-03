package woowacourse.shopping.data.remote.paging

import woowacourse.shopping.data.remote.RemoteDataSource
import woowacourse.shopping.data.remote.dto.mapper.toDomain
import woowacourse.shopping.domain.CartProduct

class ProductPagingSource(
    private val remoteDataSource: RemoteDataSource,
) {
    var offset: Int = 0
    private val pageSize: Int = 20

    fun load(offsetInput: Int = offset): LoadResult<List<CartProduct>> {
        val response = remoteDataSource.getProducts(page = offset, size = pageSize)

        if (!response.isSuccessful) {
            return LoadResult.Error(LoadErrorType.UNKNOWN)
        }

        val body = response.body() ?: return LoadResult.Error(LoadErrorType.EMPTY_BODY)
        offset++

        return LoadResult.Page(
            offset = offsetInput,
            data = body.content.map { it.toDomain() }
        )
    }
}
