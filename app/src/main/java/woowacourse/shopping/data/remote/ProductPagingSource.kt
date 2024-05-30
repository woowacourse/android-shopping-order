package woowacourse.shopping.data.remote

import woowacourse.shopping.data.remote.dto.mapper.toDomain
import woowacourse.shopping.domain.CartProduct

class ProductPagingSource(
    private val remoteDataSource: RemoteDataSource
) {

    var offset: Int = 0
    val pageSize : Int = 20

    fun load(offsetInput: Int = offset): LoadResult<List<CartProduct>> {
        val response = remoteDataSource.getProducts(page = offset, size = pageSize)
        return if(response.isSuccessful) {
            val body = response.body()
            if(body == null) return LoadResult.Error("")
            else {
                offset++
                LoadResult.Page(
                    offset = offsetInput,
                    data = body . content . map { it.toDomain() }
                )
            }
        } else {
            LoadResult.Error("")
        }
    }
}