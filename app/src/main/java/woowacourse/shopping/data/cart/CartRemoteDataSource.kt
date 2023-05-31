package woowacourse.shopping.data.cart

import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.data.cart.dto.CartProduct
import woowacourse.shopping.data.cart.dto.CartProducts
import woowacourse.shopping.data.cart.dto.ProductInsertCartRequest

class CartRemoteDataSource(baseUrl: String, private val userId: String) : CartDataSource {

    private val retrofitService: CartRetrofitService = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(CartRetrofitService::class.java)

    override fun insertCartProduct(
        productId: Long,
        quantity: Int,
        callback: (cartId: Long) -> Unit,
    ) {
        retrofitService.insertCartProduct(userId, ProductInsertCartRequest(productId, quantity))
            .enqueue(
                object : retrofit2.Callback<Unit> {
                    override fun onResponse(
                        call: Call<Unit>,
                        response: Response<Unit>,
                    ) {
                        val location = response.headers()["Location"]?.split("/")?.last()
                        callback(location?.toLong() ?: 0)
                    }

                    override fun onFailure(call: Call<Unit>, t: Throwable) {
                    }
                },
            )
    }

    override fun updateCartProduct(cartId: Long, quantity: Int, callback: () -> Unit) {
        retrofitService.updateCartProduct(userId, cartId, quantity)
            .enqueue(
                object : retrofit2.Callback<Unit> {
                    override fun onResponse(
                        call: Call<Unit>,
                        response: Response<Unit>,
                    ) {
                        callback()
                    }

                    override fun onFailure(call: Call<Unit>, t: Throwable) {
                    }
                },
            )
    }

    override fun deleteCartProduct(cartId: Long, callback: () -> Unit) {
        retrofitService.deleteCartProduct(userId, cartId)
            .enqueue(
                object : retrofit2.Callback<Unit> {
                    override fun onResponse(
                        call: Call<Unit>,
                        response: Response<Unit>,
                    ) {
                        callback()
                    }

                    override fun onFailure(call: Call<Unit>, t: Throwable) {
                    }
                },
            )
    }

    override fun getAllCartProducts(callback: (List<CartProduct>) -> Unit) {
        retrofitService.requestCartProducts(userId)
            .enqueue(
                object : retrofit2.Callback<CartProducts> {
                    override fun onResponse(
                        call: Call<CartProducts>,
                        response: Response<CartProducts>,
                    ) {
                        val cartProducts = response.body() ?: listOf()
                        callback(cartProducts)
                    }

                    override fun onFailure(call: Call<CartProducts>, t: Throwable) {
                    }
                },
            )
    }
//
//    override fun getCartProduct(productId: Long, callback: (CartProduct) -> Unit) {
//        retrofitService.(userId)
//            .enqueue(
//                object : retrofit2.Callback<CartProduct> {
//                    override fun onResponse(
//                        call: Call<CartProduct>,
//                        response: Response<CartProduct>,
//                    ) {
//                        val cartProducts = response.body() ?: listOf()
//                        callback(cartProducts)
//                    }
//
//                    override fun onFailure(call: Call<CartProduct>, t: Throwable) {
//                    }
//                },
//            )
//    }
}
