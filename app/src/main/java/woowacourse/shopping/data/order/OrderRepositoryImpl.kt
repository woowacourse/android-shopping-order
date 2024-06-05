package woowacourse.shopping.data.order

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.cart.remote.RemoteCartDataSource
import woowacourse.shopping.data.dto.response.CartQuantityResponse
import woowacourse.shopping.domain.Cart
import woowacourse.shopping.domain.repository.OrderRepository

class OrderRepositoryImpl(
    private val remoteOrderDataSource: RemoteOrderDataSource,
    private val remoteCartDataSource: RemoteCartDataSource,
) : OrderRepository {
    private fun getCount(
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

    private fun loadAll(
        onSuccess: (List<Cart>) -> Unit,
        onFailure: () -> Unit,
    ) {
        var maxCount = 0
        getCount(onSuccess = { count ->
            maxCount = count
        }, onFailure = {
            throw NoSuchElementException()
        })
    }

    override fun completeOrder(
        productIds: List<Long>,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    ) {
        val cart = mutableListOf<Cart>()
        loadAll(onSuccess = { carts ->
            cart.addAll(carts)
        }, onFailure = {})
        val cartIds = cart.filter { productIds.contains(it.product.id) }.map { it.cartId }
        remoteOrderDataSource.requestOrder(cartIds).enqueue(
            object : Callback<Unit> {
                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>,
                ) {
                    onSuccess()
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
}
