package woowacourse.shopping.data.respository.order.source.remote

import android.util.Log
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import woowacourse.shopping.data.model.OrderPostEntity
import woowacourse.shopping.data.model.Server
import woowacourse.shopping.data.respository.order.service.OrderService
import woowacouse.shopping.model.order.Order

class OrderRemoteDataSourceImpl(
    url: Server.Url,
    token: Server.Token
) : OrderRemoteDataSource {
    private val retrofit = Retrofit.Builder()
        .baseUrl(url.value)
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(OrderService::class.java)

    private val token = "Basic ${token.value}"

    override fun requestPostData(
        order: Order,
        onFailure: () -> Unit,
        onSuccess: (Long) -> Unit
    ) {
        val orderPostEntity = OrderPostEntity(
            order.cartIds,
            order.getCardNumber(),
            order.card.cvc,
            order.usePoint.getPoint()
        )
        retrofit.requestPostData(token, orderPostEntity).enqueue(object : retrofit2.Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.code() == 201) {
                    val location = response.headers()["Location"] ?: return onFailure()
                    val orderId = location.substringAfterLast("orders/").toLong()
                    onSuccess(orderId)

                    return
                }
                onFailure()
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Log.e("Request Failed", t.toString())
            }
        })
    }
}
