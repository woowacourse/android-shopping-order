package woowacourse.shopping.data.remote

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.remote.dto.request.InsertingProductDto
import woowacourse.shopping.data.remote.dto.request.RequestChargeDto
import woowacourse.shopping.data.remote.dto.request.RequestOrderDto
import woowacourse.shopping.data.remote.dto.request.UpdatingProductDto
import woowacourse.shopping.data.remote.dto.response.ProductDto
import woowacourse.shopping.data.remote.dto.response.ProductListDto
import woowacourse.shopping.data.remote.dto.response.ResponseChargeDto
import woowacourse.shopping.data.remote.dto.response.ResponseOrdersDto
import woowacourse.shopping.data.remote.dto.response.ShoppingCartDto
import woowacourse.shopping.domain.model.Order

interface ShoppingOrderService {
    @GET("/products/cart-items")
    fun getPagedProducts(
        @Query("lastId") lastId: Long,
        @Query("pageItemCount") pageItemCount: Int,
    ): Call<ProductListDto>

    @GET("/products/{id}/cart-items")
    fun getProduct(
        @Path("id") id: Long,
    ): Call<ProductDto>

    @GET("/cart-items")
    fun getAllCartItems(): Call<List<ShoppingCartDto>>

    @POST("/cart-items")
    fun insertCartItem(
        @Body insertingProductDto: InsertingProductDto,
    ): Call<Unit>

    @PATCH("/cart-items/{id}")
    fun updateCartItem(
        @Path("id") id: Long,
        @Body updatingProductDto: UpdatingProductDto,
    ): Call<Unit>

    @DELETE("/cart-items/{id}")
    fun deleteCartItem(
        @Path("id") id: Long,
    ): Call<Unit>

    @POST("/members/cash")
    fun recharge(
        @Body cashToCharge: RequestChargeDto,
    ): Call<ResponseChargeDto>

    @GET("/members/cash")
    fun getCharge(): Call<ResponseChargeDto>

    @POST("/orders")
    fun order(
        @Body requestOrderDto: RequestOrderDto,
    ): Call<Unit>

    @GET("/orders")
    fun getOrders(): Call<ResponseOrdersDto>

    @GET("/orders/{id}")
    fun getOrder(
        @Path("id") id: Long,
    ): Call<Order>
}
