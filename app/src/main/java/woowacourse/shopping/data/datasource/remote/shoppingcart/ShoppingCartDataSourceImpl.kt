package woowacourse.shopping.data.datasource.remote.shoppingcart

import woowacourse.shopping.data.datasource.remote.retrofit.RetrofitClient
import woowacourse.shopping.data.remote.request.CartItemRequest
import woowacourse.shopping.data.remote.request.CartProductDTO

class ShoppingCartDataSourceImpl :
    ShoppingCartDataSource {
    override fun getAllProductInCart(callback: (Result<List<CartProductDTO>>) -> Unit) {
        RetrofitClient.getInstance().shoppingCartService.getAllProductInCart().enqueue(
            object : retrofit2.Callback<List<CartProductDTO>> {
                override fun onResponse(
                    call: retrofit2.Call<List<CartProductDTO>>,
                    response: retrofit2.Response<List<CartProductDTO>>,
                ) {
                    if (response.isSuccessful) {
                        callback(
                            Result.success(
                                response.body() ?: throw IllegalArgumentException(),
                            ),
                        )
                    } else {
                        callback(Result.failure(Throwable(response.message())))
                    }
                }

                override fun onFailure(call: retrofit2.Call<List<CartProductDTO>>, t: Throwable) {
                    throw t
                }
            },
        )
    }

    override fun postProductToCart(productId: Long, quantity: Int, callback: (Result<Unit>) -> Unit) {
        RetrofitClient.getInstance().shoppingCartService.postProductToCart(CartItemRequest(productId, quantity)).enqueue(
            object : retrofit2.Callback<Unit> {
                override fun onResponse(
                    call: retrofit2.Call<Unit>,
                    response: retrofit2.Response<Unit>,
                ) {
                    if (response.isSuccessful) {
                        callback(Result.success(Unit))
                    } else {
                        throw IllegalArgumentException()
                    }
                }

                override fun onFailure(call: retrofit2.Call<Unit>, t: Throwable) {
                    throw t
                }
            },
        )
    }

    override fun patchProductCount(cartItemId: Long, quantity: Int, callback: (Result<Unit>) -> Unit) {
        RetrofitClient.getInstance().shoppingCartService.patchProductCount(cartItemId, quantity).enqueue(
            object : retrofit2.Callback<Unit> {
                override fun onResponse(
                    call: retrofit2.Call<Unit>,
                    response: retrofit2.Response<Unit>,
                ) {
                    if (response.isSuccessful) {
                        callback(Result.success(Unit))
                    } else {
                        throw IllegalArgumentException()
                    }
                }

                override fun onFailure(call: retrofit2.Call<Unit>, t: Throwable) {
                    throw t
                }
            },
        )
    }

    override fun deleteProductInCart(productId: Long, callback: (Result<Unit>) -> Unit) {
        RetrofitClient.getInstance().shoppingCartService.deleteProductInCart(productId).enqueue(
            object : retrofit2.Callback<Unit> {
                override fun onResponse(
                    call: retrofit2.Call<Unit>,
                    response: retrofit2.Response<Unit>,
                ) {
                    if (response.isSuccessful) {
                        callback(Result.success(Unit))
                    } else {
                        throw IllegalArgumentException()
                    }
                }

                override fun onFailure(call: retrofit2.Call<Unit>, t: Throwable) {
                    throw t
                }
            },
        )
    }
}
