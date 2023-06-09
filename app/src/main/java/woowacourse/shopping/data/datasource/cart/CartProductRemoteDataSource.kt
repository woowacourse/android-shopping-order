package woowacourse.shopping.data.datasource.cart

import android.util.Log
import retrofit2.Call
import retrofit2.Response
import woowacourse.shopping.data.dto.CartProductDto
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.data.service.cart.RetrofitCartProductService
import woowacourse.shopping.domain.model.CartProduct

class CartProductRemoteDataSource(
    private val cartProductService: RetrofitCartProductService,
) : CartProductDataSource {
    override fun requestCartProducts(
        onSuccess: (List<CartProduct>) -> Unit,
        onFailure: (String) -> Unit,
    ) {
        val call = cartProductService.requestCartProducts()
        call.enqueue(object : retrofit2.Callback<List<CartProductDto>> {
            override fun onResponse(
                call: Call<List<CartProductDto>>,
                response: Response<List<CartProductDto>>,
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    Log.d("test", "retrofit result: $result")
                    if (result != null) {
                        onSuccess(result.map { it.toDomain() })
                    }
                } else {
                    Log.d("test", "requestCartProducts NotSuccess retrofit 실패 $response")
                }
            }

            override fun onFailure(call: Call<List<CartProductDto>>, t: Throwable) {
                onFailure(t.message.toString())
            }
        })
    }

    override fun addCartProductByProductId(
        productId: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
    ) {
        val call = cartProductService.addCartProductByProductId(productId)
        call.enqueue(object : retrofit2.Callback<Int> {
            override fun onResponse(
                call: Call<Int>,
                response: Response<Int>,
            ) {
                println("${response.code()}")
                if (response.isSuccessful) {
                    Log.d("test", "retrofit result: 성공")
                    onSuccess()
                } else {
                    Log.d("test", "NotSuccess retrofit 실패")
                }
            }

            override fun onFailure(call: Call<Int>, t: Throwable) {
                onFailure(t.message.toString())
            }
        })
    }

    override fun updateCartProductCountById(
        cartItemId: String,
        quantity: Int,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
    ) {
        val call = cartProductService.updateCartProductCountById(cartItemId, quantity)
        call.enqueue(object : retrofit2.Callback<Void> {
            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>,
            ) {
                println("${response.code()}")
                if (response.isSuccessful) {
                    Log.d("test", "retrofit result: 성공")
                    onSuccess()
                } else {
                    Log.d("test", "NotSuccess retrofit 실패")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                onFailure(t.message.toString())
            }
        })
    }

    override fun deleteCartProductById(
        cartItemId: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
    ) {
        val call = cartProductService.deleteCartProductById(cartItemId)
        call.enqueue(object : retrofit2.Callback<CartProductDto> {
            override fun onResponse(
                call: Call<CartProductDto>,
                response: Response<CartProductDto>,
            ) {
                println("${response.code()}")
                if (response.isSuccessful) {
                    Log.d("test", "retrofit result: 성공")
                    onSuccess()
                } else {
                    Log.d("test", "NotSuccess retrofit 실패 ")
                }
            }

            override fun onFailure(call: Call<CartProductDto>, t: Throwable) {
                onFailure(t.message.toString())
            }
        })
    }

    override fun requestCartProductByProductId(
        productId: Int,
        onSuccess: (CartProduct) -> Unit,
        onFailure: (String) -> Unit,
    ) {
        val call = cartProductService.requestCartProductByProductId(productId.toString())
        call.enqueue(object : retrofit2.Callback<CartProductDto> {
            override fun onResponse(
                call: Call<CartProductDto>,
                response: Response<CartProductDto>,
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    Log.d("test", "retrofit result: $result")
                    if (result != null) {
                        onSuccess(result.toDomain())
                    }
                } else {
                    Log.d("test", "requestCartProductByProductId NotSuccess retrofit 실패 $response")
                }
            }

            override fun onFailure(call: Call<CartProductDto>, t: Throwable) {
                onFailure(t.message.toString())
            }
        })
    }
}
