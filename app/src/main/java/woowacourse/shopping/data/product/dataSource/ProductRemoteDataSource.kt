package woowacourse.shopping.data.product.dataSource

import woowacourse.shopping.data.product.remote.dto.ProductResponseDto
import woowacourse.shopping.data.product.remote.dto.ProductsResponseDto

interface ProductRemoteDataSource {
    fun getProducts(
        page: Int,
        size: Int,
        onCallback: (Result<ProductsResponseDto?>) -> Unit,
    )

    fun getProductDetail(
        productId: Long,
        onCallback: (Result<ProductResponseDto?>) -> Unit,
    )
}
