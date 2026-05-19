package woowacourse.shopping.data.source.remote.api

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.source.remote.dto.cart.request.AddItemRequest
import woowacourse.shopping.data.source.remote.dto.cart.request.QuantityRequest
import woowacourse.shopping.data.source.remote.dto.cart.response.CartResponse

interface CartService {
    @POST("/cart-items")
    suspend fun requestAddItem(
        @Body addItemRequest: AddItemRequest,
    )

    @GET("/cart-items")
    suspend fun requestItems(
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): CartResponse

    @DELETE("/cart-items/{id}")
    suspend fun requestDeleteItem(
        @Path("id") id: Long,
    )

    @PATCH("/cart-items/{id}")
    suspend fun requestChangeQuantity(
        @Path("id") id: Long,
        @Body quantity: QuantityRequest,
    )
}
