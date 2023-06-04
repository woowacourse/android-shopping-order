package woowacourse.shopping.presentation.view.util

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.model.CartRemoteEntity
import woowacourse.shopping.data.model.OrderDetailEntity
import woowacourse.shopping.data.model.OrderPostEntity
import woowacourse.shopping.data.model.PointEntity
import woowacourse.shopping.data.model.SavingPointEntity
import woowacourse.shopping.presentation.model.ProductModel

interface RetrofitService {
    @GET(PATH_PRODUCT)
    fun requestProducts(): Call<List<ProductModel>>

    @GET("$PATH_PRODUCT/{$PATH_ID}")
    fun requestProductById(@Path(PATH_ID) id: Long): Call<ProductModel>

    @GET(PATH_CART)
    fun requestCartProducts(@Header(AUTHORIZATION) token: String): Call<List<CartRemoteEntity>>

    @Headers(HEADER_JSON)
    @POST(PATH_CART)
    fun requestPostCartProduct(
        @Header(AUTHORIZATION) token: String,
        @Body productId: Long,
    ): Call<Unit>

    @Headers(HEADER_JSON)
    @PATCH("$PATH_CART/{$PATH_CART_ID}")
    fun requestPatchCartProduct(
        @Header(AUTHORIZATION) token: String,
        @Path(PATH_CART_ID) id: Long,
        @Body quantity: Int,
    ): Call<Unit>

    @DELETE("$PATH_CART/{$PATH_CART_ID}")
    fun requestDeleteCartProduct(
        @Header(AUTHORIZATION) token: String,
        @Path(PATH_CART_ID) id: Long,
    ): Call<Unit>

    @Headers(HEADER_JSON)
    @GET(PATH_POINT)
    fun requestReservedPoint(
        @Header(AUTHORIZATION) token: String,
    ): Call<PointEntity>

    @Headers(HEADER_JSON)
    @GET(PATH_SAVING_POINT)
    fun requestSavingPoint(
        @Query(PATH_TOTAL_PRICE) totalPrice: String,
    ): Call<SavingPointEntity>

    @Headers(HEADER_JSON)
    @POST(PATH_ORDER_POST)
    fun requestPostOrder(
        @Header(AUTHORIZATION) token: String,
        @Body orderPost: OrderPostEntity,
    ): Call<Unit>

    @GET("$PATH_ORDER/{$PATH_ORDER_ID}")
    fun requestOrder(
        @Header(AUTHORIZATION) token: String,
        @Path(PATH_ORDER_ID) orderId: Long,
    ): Call<OrderDetailEntity>

    companion object {
        private const val HEADER_JSON = "Content-Type: application/json"

        private const val PATH_PRODUCT = "/products"
        private const val PATH_CART = "/cart-items"
        private const val PATH_ID = "id"
        private const val PATH_ORDER_ID = "id"
        private const val PATH_CART_ID = "cartItemId"
        private const val PATH_TOTAL_PRICE = "totalPrice"
        private const val PATH_POINT = "/points"
        private const val PATH_SAVING_POINT = "/saving-point"
        private const val PATH_ORDER = "/orders"
        private const val PATH_ORDER_POST = "/orders"

        private const val AUTHORIZATION = "Authorization"
    }
}
