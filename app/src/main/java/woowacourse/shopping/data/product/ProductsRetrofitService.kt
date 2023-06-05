package woowacourse.shopping.data.product

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.product.dto.ProductDetail
import woowacourse.shopping.data.product.dto.ProductListInfo

interface ProductsRetrofitService {

    @GET("products/cart-items")
    fun getProductDetails(
        @Query("lastId") lastId: Long,
        @Query("pageItemCount") pageItemCount: Int,
    ): Call<ProductListInfo>

    @GET("products/{productId}/cart-items")
    fun getProductDetail(
        @Path("productId") productId: Long,
    ): Call<ProductDetail>
}
