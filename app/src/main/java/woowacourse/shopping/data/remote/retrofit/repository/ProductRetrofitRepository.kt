package woowacourse.shopping.data.remote.retrofit.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import woowacourse.shopping.data.mapper.toDomainProduct
import woowacourse.shopping.data.mapper.toDomainProducts
import woowacourse.shopping.data.remote.retrofit.api.ProductRetrofitInterface
import woowacourse.shopping.data.remote.retrofit.dto.ProductResponse
import woowacourse.shopping.domain.model.Product

class ProductRetrofitRepository(
    private val apiService: ProductRetrofitInterface,
) {

    suspend fun requestProductPage(
        page: Int,
        size: Int,
        sort: List<String>? = null,
        category: String?,
    ): ProductPageResult =
        withContext(Dispatchers.IO) {
            val response: ProductResponse =
                apiService.requestProducts(
                    page = page,
                    size = size,
                    sort = sort,
                    category = category,
                )
            ProductPageResult(
                products = response.toDomainProducts(),
                hasNextPage = !response.last,
            )
        }

    suspend fun requestProductDetail(id: Long): Product =
        withContext(Dispatchers.IO) {
            apiService.requestProductDetail(
                id = id,
            ).toDomainProduct()
        }

    data class ProductPageResult(
        val products: List<Product>,
        val hasNextPage: Boolean,
    )
}
