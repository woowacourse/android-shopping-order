package woowacourse.shopping.data.cart

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.cart.remote.RemoteCartDataSource
import woowacourse.shopping.data.dto.response.CartQuantityResponse
import woowacourse.shopping.data.dto.response.CartResponse
import woowacourse.shopping.domain.Cart
import woowacourse.shopping.domain.repository.CartRepository

class CartRepositoryImpl(
    private val remoteCartDataSource: RemoteCartDataSource = RemoteCartDataSource(),
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

    override fun loadById(
        productId: Long,
        onSuccess: (Cart?) -> Unit,
        onFailure: () -> Unit,
    ) {
        var cart: Cart?
        loadAll(
            onSuccess = { carts ->
                cart = carts.firstOrNull { it.product.id == productId }
                onSuccess(cart)
            },
            onFailure = {
                throw NoSuchElementException("productId : $productId) 장바구니에서 해당 상품을 찾을 수 없습니다.")
            },
        )
    }

    override fun deleteExistCartItem(
        productId: Long,
        onSuccess: (Long, Int) -> Unit,
        onFailure: () -> Unit,
    ) {
        loadAll(
            onSuccess = { carts ->
                val cart =
                    carts.firstOrNull { it.product.id == productId }
                        ?: throw IllegalArgumentException()
                remoteCartDataSource.delete(cart.cartId).enqueue(
                    object :
                        Callback<Unit> {
                        override fun onResponse(
                            call: Call<Unit>,
                            response: Response<Unit>,
                        ) {
                            if (response.isSuccessful) {
                                onSuccess(productId, 0)
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
            },
            onFailure = {},
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

    override fun loadAll(
        onSuccess: (List<Cart>) -> Unit,
        onFailure: () -> Unit,
    ) {
        var maxCount = 0
        getCount(onSuccess = { count ->
            maxCount = count
        }, onFailure = {
            throw NoSuchElementException()
        })
        load(
            0,
            maxCount,
            onSuccess = { carts, _ ->
                onSuccess(carts)
            },
            onFailure = {},
        )
    }

    override fun setNewCartQuantity(
        productId: Long,
        newQuantity: Int,
        onSuccess: (Long, Int) -> Unit,
        onFailure: () -> Unit,
    ) {
        loadAll(onSuccess = { carts ->
            val targetCart: Cart =
                carts.firstOrNull { it.product.id == productId } ?: run {
                    saveNewCartItem(
                        productId,
                        newQuantity,
                        onSuccess,
                        onFailure,
                    )
                    return@loadAll
                }
            when {
                0 < newQuantity ->
                    updateExistCartItem(
                        targetCart.cartId,
                        newQuantity,
                        onSuccess,
                        onFailure,
                    )

                0 == newQuantity ->
                    deleteExistCartItem(
                        productId,
                        onSuccess,
                        onFailure,
                    )

                else -> throw IllegalArgumentException()
            }
        }, onFailure = {})
    }

    override fun modifyExistCartQuantity(
        productId: Long,
        quantityDelta: Int,
        onSuccess: (Long, Int) -> Unit,
        onFailure: () -> Unit,
    ) {
        loadAll(
            onSuccess = { carts ->
                val targetCart: Cart =
                    carts.firstOrNull { it.product.id == productId } ?: run {
                        saveNewCartItem(
                            productId,
                            quantityDelta,
                            onSuccess,
                            onFailure,
                        )
                        return@loadAll
                    }
                val resultQuantity = targetCart.quantity + quantityDelta
                when {
                    0 < resultQuantity ->
                        updateExistCartItem(
                            targetCart.cartId,
                            resultQuantity,
                            onSuccess,
                            onFailure,
                        )

                    0 == resultQuantity ->
                        deleteExistCartItem(
                            productId,
                            onSuccess,
                            onFailure,
                        )

                    else -> throw java.lang.IllegalArgumentException()
                }
            },
            onFailure = {},
        )
    }
}
