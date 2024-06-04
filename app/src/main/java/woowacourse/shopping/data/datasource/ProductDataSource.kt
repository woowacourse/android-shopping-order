package woowacourse.shopping.data.datasource

import retrofit2.Call
import woowacourse.shopping.data.model.Product
import woowacourse.shopping.data.model.ProductResponse

interface ProductDataSource {
    fun getProducts(
        category: String?,
        page: Int,
        size: Int,
        sort: String,
    ): Call<ProductResponse>

    fun getProductById(id: Int): Call<Product>
}
