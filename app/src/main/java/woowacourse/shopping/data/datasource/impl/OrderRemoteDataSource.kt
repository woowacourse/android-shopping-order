package woowacourse.shopping.data.datasource.impl

import retrofit2.Call
import retrofit2.Response
import woowacourse.shopping.data.datasource.OrderDataSource
import woowacourse.shopping.data.remote.OrderApi
import woowacourse.shopping.data.remote.RetrofitGenerator
import woowacourse.shopping.data.remote.dto.OrderCartItemsDTO
import woowacourse.shopping.data.remote.dto.OrdersDTO
import woowacourse.shopping.data.remote.result.DataResult

class OrderRemoteDataSource(url: String) : OrderDataSource {
    private val orderService = RetrofitGenerator.create(url, OrderApi::class.java)

    override fun getAll(callback: (DataResult<OrdersDTO>) -> Unit) {
        orderService.requestOrders().enqueue(object : retrofit2.Callback<OrdersDTO> {
            override fun onResponse(
                call: Call<OrdersDTO>,
                response: Response<OrdersDTO>,
            ) {
                if (!response.isSuccessful) {
                    callback(DataResult.NotSuccessfulError)
                    return
                }
                response.body()?.let {
                    if (!it.isNotNull) {
                        callback(DataResult.WrongResponse)
                        return
                    }
                    callback(DataResult.Success(it))
                }
            }

            override fun onFailure(call: Call<OrdersDTO>, t: Throwable) {
                callback(DataResult.Failure)
            }
        })
    }

    override fun getOrder(id: Int, callback: (DataResult<OrdersDTO.OrderDTO>) -> Unit) {
        orderService.requestOrderDetail(id)
            .enqueue(object : retrofit2.Callback<OrdersDTO.OrderDTO> {
                override fun onResponse(
                    call: Call<OrdersDTO.OrderDTO>,
                    response: Response<OrdersDTO.OrderDTO>,
                ) {
                    if (!response.isSuccessful) {
                        callback(DataResult.NotSuccessfulError)
                        return
                    }
                    response.body()?.let {
                        if (!it.isNotNull) {
                            callback(DataResult.WrongResponse)
                            return
                        }
                        callback(DataResult.Success(it))
                    }
                }

                override fun onFailure(call: Call<OrdersDTO.OrderDTO>, t: Throwable) {
                    callback(DataResult.Failure)
                }
            })
    }

    override fun order(cartProducts: OrderCartItemsDTO, callback: (DataResult<Int?>) -> Unit) {
        orderService.requestOrderCartItems(cartProducts).enqueue(object : retrofit2.Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (!response.isSuccessful) {
                    callback(DataResult.NotSuccessfulError)
                    return
                }
                response.body()?.let {
                    val orderId = response.headers()["Location"]?.substringAfterLast("/")?.toInt()
                    if (orderId == null) {
                        callback(DataResult.WrongResponse)
                        return
                    }
                    callback(DataResult.Success(orderId))
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                callback(DataResult.Failure)
            }
        })
    }
}
