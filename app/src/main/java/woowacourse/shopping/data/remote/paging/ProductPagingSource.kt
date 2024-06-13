package woowacourse.shopping.data.remote.paging

import woowacourse.shopping.data.remote.datasource.product.ProductDataSource
import woowacourse.shopping.data.remote.dto.mapper.toDomain
import woowacourse.shopping.domain.CartProduct

class ProductPagingSource(
    private val productDataSource: ProductDataSource,
) {
    suspend fun load(
        defaultOffset: Int = 0,
        defaultPageSize: Int = 20,
    ): LoadResult<CartProduct> {
        productDataSource.getAllByPaging(page = defaultOffset, size = defaultPageSize).mapCatching {
            it.body
        }.fold(
            onSuccess = {
                return if(it == null)
                    LoadResult.Error(LoadErrorType.EMPTY_BODY)
                else
                    LoadResult.Page(
                        offset = it.number,
                        data = it.content.map { it.toDomain() },
                        last = it.last,
                    )
            },
            onFailure = {
                return LoadResult.Error(LoadErrorType.UNKNOWN)
            }
        )
    }
}
