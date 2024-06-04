package woowacourse.shopping.data.cart

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.cart.remote.RemoteCartDataSource
import woowacourse.shopping.data.dto.response.CartQuantityResponse
import woowacourse.shopping.data.dto.response.CartResponse
import woowacourse.shopping.domain.Cart
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.LocalCartDataSource

class CartRepositoryImpl(
    private val remoteCartDataSource: RemoteCartDataSource = RemoteCartDataSource(),
    private val localCartDataSource: LocalCartDataSource,
) : CartRepository {
    override fun load(
        startPage: Int,
        pageSize: Int,
        onSuccess: (List<Cart>, Int) -> Unit,
        onFailure: () -> Unit,
    ) {
        remoteCartDataSource.load(startPage, pageSize).enqueue(
            object : Callback<CartResponse> {
                override fun onResponse(
                    call: Call<CartResponse>,
                    response: Response<CartResponse>,
                ) {
                    if (response.isSuccessful) {
                        val cartData =
                            response.body() ?: run {
                                onFailure()
                                return
                            }
                        val carts = cartData.cartDto.map { it.toCart() }
                        onSuccess(carts, cartData.totalPages)
                    }
                }

                override fun onFailure(
                    call: Call<CartResponse>,
                    t: Throwable,
                ) {
                    onFailure()
                }
            },
        )
    }

    override fun saveNewCartItem(
        productId: Long,
        incrementAmount: Int,
        onSuccess: (cartId: Long, newQuantity: Int) -> Unit,
        onFailure: () -> Unit,
    ) {
        remoteCartDataSource.save(productId, incrementAmount).enqueue(
            object : Callback<Unit> {
                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>,
                ) {
                    if (response.isSuccessful) {
                        val location = response.headers()["Location"]
                        if (location != null) {
                            val segments = location.split("/")
                            val cartId = segments.last().toLong()
                            onSuccess(cartId, incrementAmount)
                        }
                    }
                }

                override fun onFailure(
                    call: Call<Unit>,
                    t: Throwable,
                ) {
                    onFailure()
                }
            },
        )
    }

    override fun updateCartItemQuantity(
        cartId: Long,
        newQuantity: Int,
        onSuccess: (Long, Int) -> Unit,
        onFailure: () -> Unit,
    ) {
        if (newQuantity == 0) {
            deleteCartItem(cartId, onSuccess, onFailure)
        } else {
            updateQuantity(cartId, newQuantity, onSuccess, onFailure)
        }
    }

    private fun updateQuantity(
        cartId: Long,
        newQuantity: Int,
        onSuccess: (Long, Int) -> Unit,
        onFailure: () -> Unit,
    ) {
        remoteCartDataSource.update(cartId, newQuantity).enqueue(
            object : Callback<Unit> {
                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>,
                ) {
                    if (response.isSuccessful) onSuccess(cartId, newQuantity)
                }

                override fun onFailure(
                    call: Call<Unit>,
                    t: Throwable,
                ) {
                    onFailure()
                }
            },
        )
    }

    override fun deleteCartItem(
        cartId: Long,
        onSuccess: (Long, Int) -> Unit,
        onFailure: () -> Unit,
    ) {
        remoteCartDataSource.delete(cartId).enqueue(
            object : Callback<Unit> {
                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>,
                ) {
                    if (response.isSuccessful) {
                        onSuccess(cartId, 0)
                    }
                }

                override fun onFailure(
                    call: Call<Unit>,
                    t: Throwable,
                ) {
                    onFailure()
                }
            },
        )
    }

    override fun getCount(
        onSuccess: (Int) -> Unit,
        onFailure: () -> Unit,
    ) {
        remoteCartDataSource.getCount().enqueue(
            object : Callback<CartQuantityResponse> {
                override fun onResponse(
                    call: Call<CartQuantityResponse>,
                    response: Response<CartQuantityResponse>,
                ) {
                    if (response.isSuccessful) {
                        val count = response.body()?.quantity ?: 0
                        onSuccess(count)
                    }
                }

                override fun onFailure(
                    call: Call<CartQuantityResponse>,
                    t: Throwable,
                ) {
                    onFailure()
                }
            },
        )
    }
}
