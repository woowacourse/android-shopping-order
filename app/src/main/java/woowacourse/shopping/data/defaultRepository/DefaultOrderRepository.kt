package woowacourse.shopping.data.defaultRepository

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.mapper.OrderMapper.toOrderItemDto
import woowacourse.shopping.data.remote.ServicePool
import woowacourse.shopping.data.remote.dto.request.RequestOrderDto
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.Order
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.util.Error
import woowacourse.shopping.domain.util.WoowaResult
import woowacourse.shopping.util.enqueueUtil

class DefaultOrderRepository : OrderRepository {
    override fun order(orderItems: List<CartProduct>, callback: (WoowaResult<Long>) -> Unit) {
        val TAG = "ORDER"
        val requestOrderDto = RequestOrderDto(orderItems.map { it.toOrderItemDto() })
        ServicePool.retrofitService.order(requestOrderDto).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    val id: Long? = response.headers()["Location"]
                        ?.substringAfterLast("/")
                        ?.toLong()
                    if (id != null) {
                        callback(WoowaResult.SUCCESS(id))
                        return
                    }
                    callback(WoowaResult.FAIL(Error.ResponseBodyNull))
                    Log.e(TAG, "code: ${response.code()}, messsage: ${response.message()}")
                    return
                }
                callback(WoowaResult.FAIL(Error.ResponseFailure))
                Log.e(TAG, "code: ${response.code()}, messsage: ${response.message()}")
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                callback(WoowaResult.FAIL(Error.ServerConnectError))
                Log.e(TAG, "throwable: ${t.cause}, messsage: ${t.message}")
            }
        })
    }

    override fun fetchOrders(callback: (WoowaResult<List<Order>>) -> Unit) {
        ServicePool.retrofitService.getOrders().enqueueUtil(
            onSuccess = { callback(WoowaResult.SUCCESS(it.orders)) },
            onFailure = { callback(WoowaResult.FAIL(it)) },
        )
    }

    override fun fetchOrder(id: Long, callback: (WoowaResult<Order>) -> Unit) {
        ServicePool.retrofitService.getOrder(id).enqueueUtil(
            onSuccess = { callback(WoowaResult.SUCCESS(it)) },
            onFailure = { callback(WoowaResult.FAIL(it)) },
        )
    }
}
