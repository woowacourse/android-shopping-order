package woowacourse.shopping.data.order

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.cart.CartRepositoryImpl
import woowacourse.shopping.data.cart.remote.RemoteCartDataSource
import woowacourse.shopping.domain.Cart
import woowacourse.shopping.domain.repository.OrderRepository

class OrderRepositoryImpl(
    private val remoteOrderDataSource: RemoteOrderDataSource,
    private val remoteCartDataSource: RemoteCartDataSource,
) : OrderRepository {
    override suspend fun completeOrder(
        productIds: List<Long>,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    ) {
        loadCartAll(onSuccess = { carts ->
            val checkedCartIds =
                carts.filter { productIds.contains(it.product.id) }.map { it.cartId }
            remoteOrderDataSource.requestOrder(checkedCartIds).enqueue(
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
        }, onFailure = {})
    }

    private suspend fun loadCartAll(
        onSuccess: (List<Cart>) -> Unit,
        onFailure: () -> Unit,
    ) {
        val cartRepository = CartRepositoryImpl()
        cartRepository.loadAll()
            .onSuccess { carts ->
                onSuccess(carts)
            }.onFailure {
                onFailure()
            }
    }
}
