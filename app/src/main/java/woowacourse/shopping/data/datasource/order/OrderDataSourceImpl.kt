package woowacourse.shopping.data.datasource.order

import android.util.Log
import retrofit2.Call
import retrofit2.Response
import woowacourse.shopping.ShoppingApplication.Companion.pref
import woowacourse.shopping.data.dto.OrderRequestDto
import woowacourse.shopping.data.dto.OrderResponseDto
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.data.mapper.toDto
import woowacourse.shopping.data.util.retrofit.RetrofitUtil
import woowacourse.shopping.domain.model.OrderRequest
import woowacourse.shopping.domain.model.OrderResponse

class OrderDataSourceImpl : OrderDataSource {
    private val baseUrl: String = pref.getBaseUrl().toString()
    private val retrofitService = RetrofitUtil.getOrderProductByRetrofit(baseUrl)

    override fun orderProducts(
        token: String,
        orderRequest: OrderRequest,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    ) {
        val call = retrofitService.orderProducts(token, orderRequest.toDto())
        call.enqueue(object : retrofit2.Callback<OrderRequestDto> {
            override fun onResponse(call: Call<OrderRequestDto>, response: Response<OrderRequestDto>) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    Log.d("test", "retrofit 실패")
                }
            }

            override fun onFailure(call: Call<OrderRequestDto>, t: Throwable) {
                Log.d("test", "onFailure retrofit 실패 ${t.message}")
                onFailure()
            }
        })
    }

    override fun requestOrders(
        token: String,
        onSuccess: (List<OrderResponse>) -> Unit,
        onFailure: () -> Unit,
    ) {
        val call = retrofitService.requestOrders(token)
        call.enqueue(object : retrofit2.Callback<List<OrderResponseDto>> {
            override fun onResponse(
                call: Call<List<OrderResponseDto>>,
                response: Response<List<OrderResponseDto>>,
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    Log.d("test", "retrofit requestOrders result : $response")
                    if (result != null) {
                        onSuccess(result.map { it.toDomain() })
                    }
                } else {
                    Log.d("test", "retrofit 실패")
                }
            }

            override fun onFailure(call: Call<List<OrderResponseDto>>, t: Throwable) {
                Log.d("test", "retrofit 실패: ${t.message}")
                onFailure()
            }
        })
    }

    override fun requestSpecificOrder(
        token: String,
        orderId: String,
        onSuccess: (OrderResponse) -> Unit,
        onFailure: () -> Unit,
    ) {
        val call = retrofitService.requestSpecificOrder(token, orderId)
        call.enqueue(object : retrofit2.Callback<OrderResponseDto> {
            override fun onResponse(call: Call<OrderResponseDto>, response: Response<OrderResponseDto>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    Log.d("test", "retrofit requestSpecificOrder result $result")
                    if (result != null) {
                        onSuccess(result.toDomain())
                    }
                } else {
                    Log.d("test", "retrofit 실패")
                }
            }

            override fun onFailure(call: Call<OrderResponseDto>, t: Throwable) {
                onFailure()
                Log.d("test", "retrofit 실패: ${t.message}")
            }
        })
    }
}
