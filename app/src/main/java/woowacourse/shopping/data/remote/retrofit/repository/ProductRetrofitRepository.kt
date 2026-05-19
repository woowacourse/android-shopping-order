package woowacourse.shopping.data.remote.retrofit.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import woowacourse.shopping.data.remote.retrofit.api.ProductRetrofitInterface
import woowacourse.shopping.data.remote.retrofit.dto.Product
import woowacourse.shopping.data.remote.retrofit.dto.ProductResponse

class ProductRetrofitRepository(
    private val apiService: ProductRetrofitInterface,
) {
    suspend fun requestProduct(
        page: Int = DEFAULT_PAGE,
        size: Int = DEFAULT_SIZE,
        sort: List<String>? = DEFAULT_SORT,
        category: String? = null,
    ): ProductResponse =
        withContext(Dispatchers.IO) {
            apiService.requestProducts(
                page = page,
                size = size,
                sort = sort,
                category = category,
            )
        }

    suspend fun requestProductDetail(id: Long): Product =
        withContext(Dispatchers.IO) {
            apiService.requestProductDetail(
                id = id,
            )
        }

    companion object {
        private const val DEFAULT_PAGE = 0
        private const val DEFAULT_SIZE = 20
        private val DEFAULT_SORT = listOf("id,asc")
    }
}
