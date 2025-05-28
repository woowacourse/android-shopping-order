package woowacourse.shopping.data.carts.repository

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.BuildConfig
import woowacourse.shopping.data.carts.CartFetchError
import woowacourse.shopping.data.carts.dto.CartItemRequest
import woowacourse.shopping.data.carts.dto.CartQuantity
import woowacourse.shopping.data.carts.dto.CartResponse
import woowacourse.shopping.data.util.RetrofitService
import woowacourse.shopping.domain.model.Authorization

class CartRemoteDataSourceImpl(
    baseUrl: String = BuildConfig.BASE_URL,
) : CartRemoteDataSource {
    private val retrofitService: RetrofitService =
        Retrofit
            .Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RetrofitService::class.java)

    override fun fetchCartItemSize(onComplete: (Int) -> Unit) {
        // Todo
    }

    override fun fetchCartItemByPage(
        page: Int,
        size: Int,
        onSuccess: (CartResponse) -> Unit,
        onFailure: (CartFetchError) -> Unit,
    ) {
        retrofitService
            .requestCartProduct(page = page, size = size, authorization = "Basic " + Authorization.basicKey)
            .enqueue(
                object : Callback<CartResponse> {
                    override fun onResponse(
                        call: Call<CartResponse>,
                        response: Response<CartResponse>,
                    ) {
                        if (response.isSuccessful && response.body() != null) {
                            onSuccess(response.body()!!)
                        } else {
                            onFailure(CartFetchError.Server(response.code(), response.message()))
                        }
                    }

                    override fun onFailure(
                        call: Call<CartResponse>,
                        t: Throwable,
                    ) {
                        onFailure(CartFetchError.Network)
                    }
                },
            )
    }

    override fun fetchCartItemByOffset(
        limit: Int,
        offset: Int,
        onSuccess: (CartResponse) -> Unit,
        onFailure: (CartFetchError) -> Unit,
    ) {
        fetchCartItemByPage(offset / limit, limit, onSuccess, onFailure)
    }

    override fun fetchCartCount(
        onSuccess: (Int) -> Unit,
        onFailure: (CartFetchError) -> Unit,
    ) {
        retrofitService
            .requestCartCounts(authorization = "Basic " + Authorization.basicKey)
            .enqueue(
                object : Callback<CartQuantity> {
                    override fun onResponse(
                        call: Call<CartQuantity>,
                        response: Response<CartQuantity>,
                    ) {
                        if (response.isSuccessful && response.body() != null) {
                            onSuccess(response.body()!!.quantity)
                        } else {
                            onFailure(CartFetchError.Server(response.code(), response.message()))
                        }
                    }

                    override fun onFailure(
                        call: Call<CartQuantity>,
                        t: Throwable,
                    ) {
                        onFailure(CartFetchError.Network)
                    }
                },
            )
    }

    override fun fetchAuthCode(onResponse: (Int) -> Unit) {
        retrofitService
            .requestCartCounts(authorization = "Basic " + Authorization.basicKey)
            .enqueue(
                object : Callback<CartQuantity> {
                    override fun onResponse(
                        call: Call<CartQuantity>,
                        response: Response<CartQuantity>,
                    ) {
                        onResponse(response.code())
                    }

                    override fun onFailure(
                        call: Call<CartQuantity>,
                        t: Throwable,
                    ) {
                        onResponse(-1)
                    }
                },
            )
    }

    override fun updateItemCount(
        cartId: Int,
        cartQuantity: CartQuantity,
        onSuccess: (resultCode: Int) -> Unit,
        onFailure: (CartFetchError) -> Unit,
    ) {
        retrofitService
            .updateCartCounts(
                cartId = cartId,
                requestBody = cartQuantity,
                authorization = "Basic " + Authorization.basicKey,
            ).enqueue(
                object : Callback<CartQuantity> {
                    override fun onResponse(
                        call: Call<CartQuantity>,
                        response: Response<CartQuantity>,
                    ) {
                        if (response.isSuccessful) {
                            onSuccess(response.code())
                        } else {
                            onFailure(CartFetchError.Server(response.code(), response.message()))
                        }
                    }

                    override fun onFailure(
                        call: Call<CartQuantity>,
                        t: Throwable,
                    ) {
                        onFailure(CartFetchError.Network)
                    }
                },
            )
    }


    override fun deleteItem(cartId : Int ,onSuccess: (resultCode: Int) -> Unit) {
        retrofitService
            .deleteCartItem(
                cartId = cartId,
                authorization = "Basic " + Authorization.basicKey,
            ).enqueue(
                object : Callback<CartQuantity> {
                    override fun onResponse(
                        call: Call<CartQuantity>,
                        response: Response<CartQuantity>,
                    ) {
                        if (response.isSuccessful) {
                            onSuccess(response.code())
                        } else {
                        }
                    }

                    override fun onFailure(
                        call: Call<CartQuantity>,
                        t: Throwable,
                    ) {
                    }
                },
            )
    }

    override fun addItem(itemId: Int) {
        retrofitService
            .addCartItem(
                cartItem = CartItemRequest(itemId,1),
                authorization = "Basic " + Authorization.basicKey,
            ).enqueue(
                object : Callback<CartQuantity> {
                    override fun onResponse(
                        call: Call<CartQuantity>,
                        response: Response<CartQuantity>,
                    ) {
                        if (response.isSuccessful) {

                        } else {
                        }
                    }

                    override fun onFailure(
                        call: Call<CartQuantity>,
                        t: Throwable,
                    ) {
                    }
                },
            )
    }

}
