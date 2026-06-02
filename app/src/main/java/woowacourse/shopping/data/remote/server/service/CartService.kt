package woowacourse.shopping.data.remote.server.service

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.remote.server.dto.cart.items.CartItemsResponse
import woowacourse.shopping.data.remote.server.dto.cart.items.PatchQuantityRequest
import woowacourse.shopping.data.remote.server.dto.cart.items.PostCartRequest
import woowacourse.shopping.data.remote.server.dto.cart.quantity.Quantity

interface CartService {
    @GET("cart-items")
    suspend fun requestCartItems(
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): CartItemsResponse

    @POST("cart-items")
    suspend fun postCartItems(
        @Body request: PostCartRequest
    )

    @GET("cart-items/counts")
    suspend fun requestQuantity(): Quantity

    @PATCH("cart-items/{id}")
    suspend fun patchQuantity(
        @Path("id") cartItemId: Long,
        @Body request: PatchQuantityRequest
    )

    @DELETE("cart-items/{id}")
    suspend fun deleteProduct(
        @Path("id") productId: Long
    )


}
