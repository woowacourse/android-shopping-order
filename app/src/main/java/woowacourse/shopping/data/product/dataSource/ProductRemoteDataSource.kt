package woowacourse.shopping.data.product.dataSource

import woowacourse.shopping.data.product.remote.dto.ProductResponseDto
import woowacourse.shopping.data.product.remote.dto.ProductsResponseDto

interface ProductRemoteDataSource {
    suspend fun getProducts(
        page: Int,
        size: Int,
        category: String? = null,
    ): ProductsResponseDto

    suspend fun getProductDetail(productId: Long): ProductResponseDto
}
