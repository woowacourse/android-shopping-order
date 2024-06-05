package woowacourse.shopping.data.datasource

import retrofit2.Call
import woowacourse.shopping.data.dto.ProductDto
import woowacourse.shopping.data.dto.ProductResponse

interface ProductDataSource {
    fun getProducts(
        category: String?,
        page: Int,
        size: Int,
        sort: String,
    ): Result<List<ProductDto>>

    fun getProductIsLast(
        category: String?,
        page: Int,
        size: Int,
        sort: String,
    ): Result<Boolean>

    fun getProductById(productId: Int): Result<ProductDto>
}
