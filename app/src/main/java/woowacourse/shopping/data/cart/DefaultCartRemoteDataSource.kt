package woowacourse.shopping.data.cart

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.entity.CartProductEntity
import woowacourse.shopping.data.entity.mapper.CartProductMapper.toDomain
import woowacourse.shopping.data.server.CartRemoteDataSource
import woowacourse.shopping.domain.CartProduct

class DefaultCartRemoteDataSource(private val service: CartService): CartRemoteDataSource {
    override fun getCartProducts(onSuccess: (List<CartProduct>) -> Unit, onFailure: (String) -> Unit) {
        service.requestCartProducts().enqueue(object : Callback<List<CartProductEntity>> {
            override fun onResponse(
                call: Call<List<CartProductEntity>>,
                response: Response<List<CartProductEntity>>
            ) {
                if(response.isSuccessful) {
                    onSuccess(response.body()?.map { it.toDomain() } ?: emptyList())
                }else {
                    onFailure(response.message().ifBlank { MESSAGE_GET_PRODUCTS_FAILED })
                }
            }

            override fun onFailure(call: Call<List<CartProductEntity>>, t: Throwable) {
                onFailure(MESSAGE_GET_PRODUCTS_FAILED)
            }
        })
    }

    override fun addCartProduct(id: Int, quantity: Int, onSuccess: (Int) -> Unit, onFailure: (String) -> Unit) {
        val json = JSONObject()
            .put("productId", id)
            .put("quantity", quantity)
        val body = json.toString().toRequestBody("application/json".toMediaType())
        service.createCartProduct(body).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                val header = response.headers()
                val cartId = header["Location"]?.substringAfterLast("/")?.toIntOrNull()
                if(response.isSuccessful && cartId != null) {
                    onSuccess(cartId)
                }
                else {
                    onFailure(response.message().ifBlank { MESSAGE_ADD_PRODUCT_FAILED })
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                onFailure(MESSAGE_ADD_PRODUCT_FAILED)
            }
        })
    }

    override fun updateCartProductQuantity(
        id: Int,
        quantity: Int,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val json = JSONObject()
            .put("quantity", quantity)
        val body = json.toString().toRequestBody("application/json".toMediaType())
        service.updateCartProductQuantity(id, body).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if(response.isSuccessful) {
                    onSuccess()
                }
                else {
                    onFailure(response.message().ifBlank { MESSAGE_UPDATE_QUANTITY_FAILED })
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                onFailure(MESSAGE_UPDATE_QUANTITY_FAILED)
            }
        })
    }

    override fun deleteCartProduct(
        cartProductId: Int,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        service.deleteCartProduct(cartProductId).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if(response.isSuccessful) {
                    onSuccess()
                }
                else {
                    onFailure(response.message().ifBlank { MESSAGE_DELETE_FAILED })
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                onFailure(MESSAGE_DELETE_FAILED)
            }
        })
    }

    companion object {
        private const val MESSAGE_GET_PRODUCTS_FAILED = "상품을 불러오는데 실패했습니다."
        private const val MESSAGE_ADD_PRODUCT_FAILED = "장바구니에 상품을 추가하는데 실패했습니다."
        private const val MESSAGE_UPDATE_QUANTITY_FAILED = "장바구니 상품 수량을 업데이트하는데 실패했습니다"
        private const val MESSAGE_DELETE_FAILED = "장바구니 상품을 삭제하는데 실패했습니다"
    }
}