package woowacourse.shopping.data.datasource.cart

import android.util.Log
import retrofit2.Call
import retrofit2.Response
import woowacourse.shopping.ShoppingApplication.Companion.pref
import woowacourse.shopping.data.dto.CartProductDto
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.data.util.retrofit.RetrofitUtil.getCartProductByRetrofit
import woowacourse.shopping.domain.model.CartProduct

class CartProductDataSourceImpl : CartProductDataSource {
    private val baseUrl: String = pref.getBaseUrl().toString()
    private val retrofitService = getCartProductByRetrofit(baseUrl)

    override fun requestCartProducts(
        token: String,
        onSuccess: (List<CartProduct>) -> Unit,
        onFailure: () -> Unit,
    ) {
        val call = retrofitService.requestCartProducts(token)
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
                Log.d("test", "onFailure retrofit 실패: ${t.message}, $token")
                onFailure()
            }
        })
    }

    override fun addCartProductByProductId(
        token: String,
        productId: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    ) {
        val call = retrofitService.addCartProductByProductId(token, productId)
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
                    Log.d("test", "NotSuccess retrofit 실패 $token")
                }
            }

            override fun onFailure(call: Call<Int>, t: Throwable) {
                Log.d("test", "onFailure retrofit 실패: ${t.message}, $token")
                onFailure()
            }
        })
    }

    override fun updateCartProductCountById(
        token: String,
        cartItemId: String,
        quantity: Int,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    ) {
        val call = retrofitService.updateCartProductCountById(token, cartItemId, quantity)
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
                    Log.d("test", "NotSuccess retrofit 실패 , $token")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("test", "onFailure retrofit 실패: ${t.message}, $token")
                onFailure()
            }
        })
    }

    override fun deleteCartProductById(
        token: String,
        cartItemId: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    ) {
        val call = retrofitService.deleteCartProductById(token, cartItemId)
        call.enqueue(object : retrofit2.Callback<CartProductDto> {
            override fun onResponse(
                call: Call<CartProductDto>,
                response: Response<CartProductDto>,
            ) {
                println("${response.code()}")
                if (response.isSuccessful) {
                    Log.d("test", "retrofit result: 성공, $token")
                    onSuccess()
                } else {
                    Log.d("test", "NotSuccess retrofit 실패 ")
                }
            }

            override fun onFailure(call: Call<CartProductDto>, t: Throwable) {
                Log.d("test", "onFailure retrofit 실패: ${t.message}, $token")
                onFailure()
            }
        })
    }

    override fun requestCartProductByProductId(
        token: String,
        productId: Int,
        onSuccess: (CartProduct) -> Unit,
        onFailure: () -> Unit,
    ) {
        val call = retrofitService.requestCartProductByProductId(token, productId.toString())
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
                Log.d("test", "onFailure retrofit 실패: ${t.message}, $token")
                onFailure()
            }
        })
    }
}
