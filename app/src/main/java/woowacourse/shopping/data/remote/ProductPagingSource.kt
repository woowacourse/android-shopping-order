package woowacourse.shopping.data.remote

import woowacourse.shopping.data.remote.dto.mapper.toDomain
import woowacourse.shopping.domain.CartProduct

class ProductPagingSource(
    private val remoteDataSource: RemoteDataSource,
) {
    var offset: Int = 0
    val pageSize: Int = 20

    fun load(
        offsetInput: Int = offset,
        callback: (LoadResult<List<CartProduct>>) -> Unit,
    ) {
        remoteDataSource.getProducts(page = offset, size = pageSize) {
            if (it.isSuccess) {
                val body = it.getOrNull()
                if (body == null) {
                    callback(LoadResult.Error(""))
                } else {
                    offset++
                    callback(
                        LoadResult.Page(
                            offset = offsetInput,
                            data = body.content.map { it.toDomain() },
                        ),
                    )
                }
            } else {
                callback(LoadResult.Error(""))
            }
        }
    }
}
