package woowacourse.shopping.backend.retrofit.repository

import retrofit2.Call
import woowacourse.shopping.backend.retrofit.api.ProductRetrofitInterface
import woowacourse.shopping.backend.retrofit.dto.Product
import woowacourse.shopping.backend.retrofit.dto.ProductResponse

class ProductRetrofitRepository(
    private val apiService: ProductRetrofitInterface,
) {
    fun requestProduct(
        page: Int = DEFAULT_PAGE,
        size: Int = DEFAULT_SIZE,
        sort: List<String>? = null,
        category: String? = null,
    ): Call<ProductResponse> =
        apiService.requestProducts(
            page = page,
            size = size,
            sort = sort,
            category = category,
        )

    fun requestProductDetail(id: Long): Call<Product> =
        apiService.requestProductDetail(
            id = id,
        )

    fun addProduct(product: Product): Call<Void> =
        apiService.addProduct(
            product = product,
        )

    fun deleteProduct(id: Long): Call<Void> =
        apiService.deleteProduct(
            id = id,
        )

    companion object {
        private const val DEFAULT_PAGE = 0
        private const val DEFAULT_SIZE = 20
    }
}
