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
            object :
                Callback<CartResponse> {
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

    override fun updateIncrementQuantity(
        cartId: Long,
        productId: Long,
        incrementAmount: Int,
        quantity: Int,
        onSuccess: (Long, Int) -> Unit,
        onFailure: () -> Unit,
    ) {
        if (cartId == -1L) {
            saveNewCartItem(productId, incrementAmount, onSuccess, onFailure)
        } else {
            updateExistCartItem(cartId, quantity + incrementAmount, onSuccess, onFailure)
        }
    }

    override fun updateDecrementQuantity(
        cartId: Long,
        productId: Long,
        decrementAmount: Int,
        quantity: Int,
        onSuccess: (Long, Int) -> Unit,
        onFailure: () -> Unit,
    ) {
        if (cartId == -1L) {
            throw IllegalArgumentException()
        }
        val resultQuantity = quantity - decrementAmount
        if (resultQuantity == 0) {
            deleteExistCartItem(
                cartId,
                onSuccess,
                onFailure,
            )
        } else {
            updateExistCartItem(cartId, quantity - decrementAmount, onSuccess, onFailure)
        }
    }

    override fun deleteExistCartItem(
        cartId: Long,
        onSuccess: (Long, Int) -> Unit,
        onFailure: () -> Unit,
    ) {
        remoteCartDataSource.delete(cartId).enqueue(
            object :
                Callback<Unit> {
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

    private fun updateExistCartItem(
        cartId: Long,
        resultQuantity: Int,
        onSuccess: (Long, Int) -> Unit,
        onFailure: () -> Unit,
    ) {
        remoteCartDataSource.update(cartId, resultQuantity).enqueue(
            object :
                Callback<Unit> {
                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>,
                ) {
                    if (response.isSuccessful) onSuccess(cartId, resultQuantity)
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

    private fun saveNewCartItem(
        productId: Long,
        incrementAmount: Int,
        onSuccess: (Long, Int) -> Unit,
        onFailure: () -> Unit,
    ) {
        remoteCartDataSource.save(productId, incrementAmount).enqueue(
            object :
                Callback<Unit> {
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

    override fun getCount(
        onSuccess: (Int) -> Unit,
        onFailure: () -> Unit,
    ) {
        remoteCartDataSource.getCount().enqueue(
            object :
                Callback<CartQuantityResponse> {
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
