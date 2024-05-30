package woowacourse.shopping.data.order

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.domain.repository.OrderRepository

class OrderRepositoryImpl(private val remoteOrderDataSource: RemoteOrderDataSource) : OrderRepository {
    override fun completeOrder(
        cartItemIds: List<Long>,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    ) {
        val cartIds = cartItemIds.map { it.toInt() }
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
