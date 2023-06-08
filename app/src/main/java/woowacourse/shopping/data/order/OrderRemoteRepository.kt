package woowacourse.shopping.data.order

import com.example.domain.FixedDiscountPolicy
import com.example.domain.order.Order
import com.example.domain.order.OrderRepository
import com.example.domain.order.OrderSummary
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.data.order.model.dto.request.OrderRequest
import woowacourse.shopping.data.order.model.dto.response.FixedDiscountPolicyResponse
import woowacourse.shopping.data.order.model.dto.response.OrderSummaryResponse
import woowacourse.shopping.data.order.model.toDomain
import woowacourse.shopping.util.BANDAL

class OrderRemoteRepository(
    private val user: String = BANDAL,
    url: String,
    port: String = "8080",
) : OrderRepository {

    private val authorizationHttpClient = OkHttpClient.Builder().apply {
        addInterceptor { chain ->
            val interceptedRequest = chain.request()
            val headerAddedRequest =
                interceptedRequest.newBuilder().header("Authorization", "Basic $user")
                    .build()
            chain.proceed(headerAddedRequest)
        }
//        val loggingInterceptor = HttpLoggingInterceptor()
//        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
//        addInterceptor(loggingInterceptor)
    }.build()

    private val baseUrl = "$url:$port"
    private val retrofitOrderService: RetrofitOrderService = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(authorizationHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(RetrofitOrderService::class.java)

    override fun requestFetchAllOrders(
        onSuccess: (List<OrderSummary>) -> Unit,
        onFailure: () -> Unit
    ) {
        retrofitOrderService.requestAllOrders().enqueue(
            object : Callback<List<OrderSummaryResponse>> {
                override fun onResponse(
                    call: Call<List<OrderSummaryResponse>>,
                    response: Response<List<OrderSummaryResponse>>
                ) {
                    val result = response.body() ?: emptyList()
                    if (400 <= response.code()) return onFailure()
                    onSuccess(result.map(OrderSummaryResponse::toDomain))
                }

                override fun onFailure(call: Call<List<OrderSummaryResponse>>, t: Throwable) {
                    onFailure()
                }
            })
    }

    override fun requestAddOrder(
        cartIds: List<Long>,
        finalPrice: Int,
        onSuccess: (orderId: Long) -> Unit,
        onFailure: () -> Unit
    ) {
        retrofitOrderService.requestAddOrder(request = OrderRequest(cartIds, finalPrice))
            .enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    val resultOrderId: Long? =
                        response.headers()["Location"]?.split("/")?.last()?.toLong()
                    if (400 <= response.code()) return onFailure()
                    if (resultOrderId == null) return onFailure()
                    onSuccess(resultOrderId)
                }
                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    onFailure()
                }
            })
    }

    override fun requestFetchOrderById(
        id: Long,
        onSuccess: (order: Order?) -> Unit,
        onFailure: () -> Unit
    ) {
        retrofitOrderService.requestFetchOrderById(
            id = id
        ).enqueue(object : Callback<Order> {
            override fun onResponse(call: Call<Order>, response: Response<Order>) {
                val resultOrder: Order? = response.body()
                if (400 <= response.code()) return onFailure()
                if (resultOrder == null) return onFailure()
                onSuccess(resultOrder)
            }

            override fun onFailure(call: Call<Order>, t: Throwable) {
                onFailure()
            }
        })
    }

    override fun requestFetchDiscountPolicy(
        onSuccess: (fixedDiscountPolicy: FixedDiscountPolicy) -> Unit,
        onFailure: () -> Unit
    ) {
        retrofitOrderService.requestFetchDiscountPolicy()
            .enqueue(object : Callback<FixedDiscountPolicyResponse> {
                override fun onResponse(
                    call: Call<FixedDiscountPolicyResponse>,
                    response: Response<FixedDiscountPolicyResponse>
                ) {
                    val result: FixedDiscountPolicyResponse = response.body() ?: return onFailure()
                    if (400 <= response.code()) return onFailure()
                    onSuccess(result.toDomain())
                }

                override fun onFailure(call: Call<FixedDiscountPolicyResponse>, t: Throwable) {
                    onFailure()
                }
            }
            )
    }
}