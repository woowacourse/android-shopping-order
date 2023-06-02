package woowacourse.shopping.data.cart

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import woowacourse.shopping.Storage
import woowacourse.shopping.data.entity.CartProductEntity
import woowacourse.shopping.data.entity.mapper.CartProductMapper.toDomain
import woowacourse.shopping.data.server.CartRemoteDataSource
import woowacourse.shopping.data.server.Server
import woowacourse.shopping.domain.CartProduct

class CartRemoteDataSourceRetrofit: CartRemoteDataSource {
    private val cartService: CartService = Retrofit.Builder()
        .baseUrl(Server.getUrl(Storage.server))
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(CartService::class.java)

    override fun getCartProducts(onSuccess: (List<CartProduct>) -> Unit, onFailure: () -> Unit) {
        cartService.requestCartProducts(Storage.credential).enqueue(object : retrofit2.Callback<List<CartProductEntity>> {
            override fun onResponse(
                call: Call<List<CartProductEntity>>,
                response: Response<List<CartProductEntity>>
            ) {
                if(response.isSuccessful) {
                    onSuccess(response.body()?.map { it.toDomain() } ?: emptyList())
                }else {
                    onFailure()
                }
            }

            override fun onFailure(call: Call<List<CartProductEntity>>, t: Throwable) {
                onFailure()
            }
        })
    }

    override fun addCartProduct(id: Int, quantity: Int, onSuccess: (Int) -> Unit, onFailure: () -> Unit) {
        val json = JSONObject()
            .put("productId", id)
            .put("quantity", quantity)
        val body = json.toString().toRequestBody("application/json".toMediaType())
        cartService.createCartProduct(Storage.credential, body).enqueue(object : retrofit2.Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                val header = response.headers()
                val cartId = header["Location"]?.substringAfterLast("/")?.toIntOrNull()
                if(response.isSuccessful && cartId != null) {
                    onSuccess(cartId)
                }
                else {
                    onFailure()
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                onFailure()
            }
        })
    }

    override fun updateCartProductQuantity(
        id: Int,
        quantity: Int,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun deleteCartProduct(
        cartProductId: Int,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        TODO("Not yet implemented")
    }

}