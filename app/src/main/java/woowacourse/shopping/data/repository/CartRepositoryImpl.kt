package woowacourse.shopping.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import woowacourse.shopping.data.model.CartItemRequestBody
import woowacourse.shopping.data.model.CartQuantity
import woowacourse.shopping.data.model.toCartDomain
import woowacourse.shopping.data.datasource.DefaultRemoteCartDataSource
import woowacourse.shopping.data.datasource.RemoteCartDataSource
import woowacourse.shopping.data.model.CartResponse
import woowacourse.shopping.domain.model.CartDomain
import woowacourse.shopping.domain.repository.CartRepository
import kotlin.concurrent.thread

class CartRepositoryImpl(
    private val remoteCartDataSource: RemoteCartDataSource,
) : CartRepository {
    override fun getCartItems(
        page: Int,
        size: Int,
        sort: String,
        onSuccess: (CartDomain) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        remoteCartDataSource.getCartItems(page, size, sort).enqueue(object : Callback<CartResponse> {
            override fun onResponse(call: Call<CartResponse>, response: Response<CartResponse>) {
                val cartDomain = response.body()?.toCartDomain() ?: throw HttpException(response)
                onSuccess(cartDomain)
            }

            override fun onFailure(call: Call<CartResponse>, t: Throwable) {
                onFailure(t)
            }

        })
//        thread {
//            runCatching {
//                val response = remoteCartDataSource.getCartItems(page, size, sort).execute()
//                response.body()?.toCartDomain() ?: throw HttpException(response)
//            }.onSuccess(onSuccess).onFailure(onFailure)
//        }
    }

    override fun addCartItem(
        productId: Int,
        quantity: Int,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        remoteCartDataSource.addCartItem(CartItemRequestBody(productId, quantity)).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.code() != 201) throw HttpException(response)
                onSuccess()
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                onFailure(t)
            }
        })
//        thread {
//            runCatching {
//                val response =
//                    remoteCartDataSource.addCartItem(CartItemRequestBody(productId, quantity))
//                        .execute()
//                if (response.code() != 201) throw HttpException(response)
//            }.onSuccess {
//                onSuccess()
//            }.onFailure(onFailure)
//        }
    }

    override fun deleteCartItem(
        cartItemId: Int,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        remoteCartDataSource.deleteCartItem(cartItemId).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.code() != 204) throw HttpException(response)
                onSuccess()
            }
            override fun onFailure(call: Call<Unit>, t: Throwable) {
                onFailure(t)
            }
        })
    }

    override fun updateCartItem(
        cartItemId: Int,
        quantity: Int,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        remoteCartDataSource.updateCartItem(cartItemId, CartQuantity(quantity)).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.code() != 200) throw HttpException(response)
                onSuccess()
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                onFailure(t)
            }
        })
//        thread {
//            runCatching {
//                val response =
//                    remoteCartDataSource.updateCartItem(cartItemId, CartQuantity(quantity))
//                        .execute()
//                if (response.code() != 200) throw HttpException(response)
//            }.onSuccess {
//                onSuccess()
//            }.onFailure(onFailure)
//        }
    }

    override fun getCartTotalQuantity(onSuccess: (Int) -> Unit, onFailure: (Throwable) -> Unit) {
        remoteCartDataSource.getCartTotalQuantity().enqueue(object : Callback<CartQuantity> {
            override fun onResponse(call: Call<CartQuantity>, response: Response<CartQuantity>) {
                val quantity = response.body()?.quantity ?: throw HttpException(response)
                onSuccess(quantity)
            }

            override fun onFailure(call: Call<CartQuantity>, t: Throwable) {
                onFailure(t)
            }

        })
//        thread {
//            runCatching {
//                val response = remoteCartDataSource.getCartTotalQuantity().execute()
//                response.body()?.quantity ?: throw HttpException(response)
//            }.onSuccess { quantity ->
//                onSuccess(quantity)
//            }.onFailure(onFailure)
//        }
    }
}
