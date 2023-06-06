package woowacourse.shopping.data.cart

import android.util.Log
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import woowacourse.shopping.data.cart.dto.CartProduct
import woowacourse.shopping.data.cart.dto.ProductInsertCartRequest
import woowacourse.shopping.data.util.RetrofitCallback

class CartRemoteDataSource(retrofit: Retrofit) : CartDataSource {

    private val retrofitService = retrofit.create(CartRetrofitService::class.java)

    override fun insertCartProduct(
        productId: Long,
        quantity: Int,
        callback: (cartId: Long) -> Unit,
    ) {
        retrofitService.insertCartProduct(ProductInsertCartRequest(productId, quantity))
            .enqueue(
                object : retrofit2.Callback<Unit> {
                    override fun onResponse(
                        call: Call<Unit>,
                        response: Response<Unit>,
                    ) {
                        val location = response.headers()[LOCATION]?.split("/")?.last()
                        callback(location?.toLong() ?: 0)
                    }

                    override fun onFailure(call: Call<Unit>, t: Throwable) {
                        Log.e("Request Failed", t.toString())
                    }
                },
            )
    }

    override fun updateCartProduct(cartId: Long, quantity: Int, callback: () -> Unit) {
        retrofitService.updateCartProduct(cartId, quantity)
            .enqueue(
                object : RetrofitCallback<Unit>() {
                    override fun onSuccess(response: Unit?) {
                        callback()
                    }
                },
            )
    }

    override fun deleteCartProduct(cartId: Long, callback: () -> Unit) {
        retrofitService.deleteCartProduct(cartId)
            .enqueue(
                object : RetrofitCallback<Unit>() {
                    override fun onSuccess(response: Unit?) {
                        callback()
                    }
                },
            )
    }

    override fun getAllCartProducts(callback: (List<CartProduct>) -> Unit) {
        retrofitService.requestCartProducts()
            .enqueue(
                object : RetrofitCallback<List<CartProduct>>() {
                    override fun onSuccess(response: List<CartProduct>?) {
                        callback(response ?: listOf())
                    }
                },
            )
    }

    companion object {
        private const val LOCATION = "Location"
    }
}
