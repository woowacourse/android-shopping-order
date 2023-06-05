package woowacourse.shopping.data.service

import com.example.domain.model.OrderDetailProduct
import com.example.domain.model.OrderHistoryInfo
import com.example.domain.model.OrderInfo
import com.example.domain.model.Point
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import woowacourse.shopping.data.model.OrderDetailDto
import woowacourse.shopping.data.model.OrderDetailProductDto
import woowacourse.shopping.data.model.OrderHistoryInfoDto
import woowacourse.shopping.data.model.OrderProductDto
import woowacourse.shopping.data.model.toDomain

class OrderRemoteService(private val credential: String) {

    private val retrofitService = Retrofit.Builder()
        .baseUrl(ServerInfo.currentBaseUrl)
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(RetrofitService::class.java)

    fun addOrder(
        usedPoint: Point,
        orderDetailProduct: List<OrderDetailProduct>,
        callback: (Result<Unit>) -> Unit
    ) {
        val orderProducts = orderDetailProduct.map {
            OrderProductDto(
                it.product.id.toInt(),
                it.quantity
            )
        }
        val orderInfo = OrderDetailProductDto(usedPoint.value, orderProducts)
        retrofitService.addOrder(
            "Basic $credential",
            orderInfo
        ).enqueue(object : retrofit2.Callback<Unit> {
            override fun onResponse(
                call: retrofit2.Call<Unit>,
                response: retrofit2.Response<Unit>
            ) {
                if (response.code() >= 400) return onFailure(call, Throwable("문제가 발생하였습니다."))
                callback(Result.success(Unit))
            }

            override fun onFailure(call: retrofit2.Call<Unit>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }

    fun cancelOrder(
        orderId: Int,
        callback: (Result<Unit>) -> Unit
    ) {
        retrofitService.cancelOrder(
            "Basic $credential",
            orderId
        ).enqueue(object : retrofit2.Callback<Unit> {
            override fun onResponse(
                call: retrofit2.Call<Unit>,
                response: retrofit2.Response<Unit>
            ) {
                if (response.code() >= 400) return onFailure(call, Throwable("문제가 발생하였습니다."))
                callback(Result.success(Unit))
            }

            override fun onFailure(call: retrofit2.Call<Unit>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }

    fun loadAll(
        pageNum: Int,
        callback: (Result<OrderHistoryInfo>) -> Unit
    ) {
        retrofitService.requestOrderHistory(
            "Basic $credential", pageNum
        ).enqueue(object : retrofit2.Callback<OrderHistoryInfoDto> {
            override fun onResponse(
                call: retrofit2.Call<OrderHistoryInfoDto>,
                response: retrofit2.Response<OrderHistoryInfoDto>
            ) {
                if (response.code() >= 400) return onFailure(call, Throwable("문제가 발생하였습니다."))
                val result = response.body()
                if (result != null) {
                    callback(Result.success(result.toDomain()))
                }
            }

            override fun onFailure(call: retrofit2.Call<OrderHistoryInfoDto>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }

    fun loadDetail(
        detailId: Int,
        callback: (Result<OrderInfo>) -> Unit
    ) {
        retrofitService.requestOrderDetail(
            "Basic $credential", detailId
        ).enqueue(object : retrofit2.Callback<OrderDetailDto> {
            override fun onResponse(
                call: retrofit2.Call<OrderDetailDto>,
                response: retrofit2.Response<OrderDetailDto>
            ) {
                if (response.code() >= 400) return onFailure(call, Throwable("문제가 발생하였습니다."))
                val result = response.body()
                if (result != null) {
                    callback(Result.success(result.toDomain()))
                }
            }

            override fun onFailure(call: retrofit2.Call<OrderDetailDto>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }
}
