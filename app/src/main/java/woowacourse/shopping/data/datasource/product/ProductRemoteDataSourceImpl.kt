package woowacourse.shopping.data.datasource.product

import woowacourse.shopping.data.remote.api.ProductApi
import woowacourse.shopping.data.remote.dto.ProductResponseDto
import woowacourse.shopping.data.remote.dto.ProductsResponseDto

class ProductRemoteDataSourceImpl(
    private val productApi: ProductApi,
) : ProductRemoteDataSource {
    override suspend fun getProducts(
        page: Int,
        size: Int,
    ): ProductsResponseDto = productApi.getProducts(page = page, size = size)

    override suspend fun getCategoryProducts(
        category: String?,
        page: Int,
        size: Int,
    ): ProductsResponseDto = productApi.getProducts(category = category, page = page, size = size)

    override suspend fun getProduct(id: Int): ProductResponseDto = productApi.getProductDetail(id)
}
