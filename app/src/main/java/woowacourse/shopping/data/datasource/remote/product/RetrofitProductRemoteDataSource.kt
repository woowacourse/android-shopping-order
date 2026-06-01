package woowacourse.shopping.data.datasource.remote.product

import woowacourse.shopping.data.mapper.toDomainProduct
import woowacourse.shopping.data.mapper.toDomainProducts
import woowacourse.shopping.data.remote.retrofit.api.ProductRetrofitInterface
import woowacourse.shopping.data.remote.retrofit.dto.ProductResponse
import woowacourse.shopping.domain.model.Product

class RetrofitProductRemoteDataSource(
    private val apiService: ProductRetrofitInterface,
) : ProductRemoteDataSource {
    override suspend fun requestProductPage(
        page: Int,
        size: Int,
        sort: List<String>?,
        category: String?,
    ): ProductRemoteDataSource.ProductPageResult {
        val response: ProductResponse =
            apiService.requestProducts(
                page = page,
                size = size,
                sort = sort,
                category = category,
            )
        return ProductRemoteDataSource.ProductPageResult(
            products = response.toDomainProducts(),
            hasNextPage = !response.last,
        )
    }

    override suspend fun requestProductDetail(id: Long): Product =
        apiService
            .requestProductDetail(
                id = id,
            ).toDomainProduct()
}
