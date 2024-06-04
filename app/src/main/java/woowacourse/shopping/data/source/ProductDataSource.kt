package woowacourse.shopping.data.source

import retrofit2.Call
import woowacourse.shopping.data.remote.dto.product.ProductDto
import woowacourse.shopping.data.remote.dto.product.ProductResponse
import woowacourse.shopping.domain.model.Product

interface ProductDataSource {
    fun loadProducts(
        page: Int,
        size: Int,
    ): Result<List<Product>>

    fun loadCategoryProducts(
        page: Int,
        size: Int,
        category: String,
    ): Result<List<Product>>

    fun loadProduct(id: Int): Result<Product>
}
