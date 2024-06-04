package woowacourse.shopping.data.source

import retrofit2.Call
import woowacourse.shopping.data.remote.dto.product.ProductDto
import woowacourse.shopping.data.remote.dto.product.ProductResponse
import woowacourse.shopping.domain.model.Product

interface ProductDataSource {
    suspend fun loadProducts(
        page: Int,
        size: Int,
    ): Result<List<Product>>

    suspend fun loadCategoryProducts(
        page: Int,
        size: Int,
        category: String,
    ): Result<List<Product>>

    suspend fun loadProduct(id: Int): Result<Product>
}
