package woowacourse.shopping.data.source

import retrofit2.Call
import woowacourse.shopping.data.remote.dto.product.ProductDto
import woowacourse.shopping.data.remote.dto.product.ProductResponse
import woowacourse.shopping.domain.model.Product

interface ProductDataSource {
    fun loadProducts(
        page: Int,
        size: Int,
    ): Call<ProductResponse>

    fun loadCategoryProducts(
        page: Int,
        size: Int,
        category: String,
    ): Call<ProductResponse>

    fun loadProduct(
        id: Int
    ): Call<ProductDto>
}
