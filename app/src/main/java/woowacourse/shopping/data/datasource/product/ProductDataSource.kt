package woowacourse.shopping.data.datasource.product

import retrofit2.Call
import woowacourse.shopping.data.dto.ProductDto

interface ProductDataSource {
    fun requestProducts(): Call<List<ProductDto>>
    fun requestProductById(productId: String): Call<ProductDto?>
    fun insertProduct(product: ProductDto): Call<ProductDto>
    fun updateProduct(productId: String, product: ProductDto): Call<ProductDto>
    fun deleteProduct(productId: String): Call<ProductDto>
}
