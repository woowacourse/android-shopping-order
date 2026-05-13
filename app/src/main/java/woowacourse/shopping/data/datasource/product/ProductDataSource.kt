package woowacourse.shopping.data.datasource.product

import woowacourse.shopping.data.remote.dto.ProductResponseDto
import woowacourse.shopping.data.remote.dto.ProductsResponseDto

interface ProductDataSource {
    suspend fun getProducts(
        page: Int,
        size: Int,
    ): ProductsResponseDto

    suspend fun getProduct(id: Int): ProductResponseDto?
}
