package woowacourse.shopping.data.source.remote

import woowacourse.shopping.data.source.remote.api.ProductService
import woowacourse.shopping.data.source.remote.api.safeNetworkApiCall
import woowacourse.shopping.data.source.remote.dto.product.response.ProductContent
import woowacourse.shopping.error.NetworkError
import woowacourse.shopping.error.Result

class ProductRemoteDataSource(
    private val productService: ProductService,
) {
    suspend fun fetchProducts(
        page: Int,
        size: Int,
    ): Result<List<ProductContent>, NetworkError> =
        safeNetworkApiCall {
            productService
                .requestProducts(
                    page = page,
                    size = size,
                ).content
        }
}
