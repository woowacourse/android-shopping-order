package woowacourse.shopping.data.datasource.remote.product

import woowacourse.shopping.data.mapper.toDomainProduct
import woowacourse.shopping.data.mapper.toDomainProducts
import woowacourse.shopping.data.remote.retrofit.api.ProductRetrofitInterface
import woowacourse.shopping.data.remote.retrofit.dto.ProductResponse
import woowacourse.shopping.domain.model.Product

class ProductRemoteDataSource(
    private val apiService: ProductRetrofitInterface,
) {
    suspend fun requestProductPage(
        page: Int,
        size: Int,
        sort: List<String>? = null,
        category: String?,
    ): ProductPageResult {
        val response: ProductResponse =
            apiService.requestProducts(
                page = page,
                size = size,
                sort = sort,
                category = category,
            )
        return ProductPageResult(
            products = response.toDomainProducts(),
            hasNextPage = !response.last,
        )
    }

    suspend fun requestProductDetail(id: Long): Product =
        apiService.requestProductDetail(
            id = id,
        ).toDomainProduct()

    data class ProductPageResult(
        val products: List<Product>,
        val hasNextPage: Boolean,
    )
}
