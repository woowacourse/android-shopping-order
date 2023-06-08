package woowacourse.shopping.data.order

import android.util.Log
import com.example.domain.FixedDiscountPolicy
import com.example.domain.order.Order
import com.example.domain.order.OrderRepository
import com.example.domain.order.OrderSummary
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.create
import woowacourse.shopping.data.cart.model.toDomain
import woowacourse.shopping.data.order.model.dto.request.OrderRequest
import woowacourse.shopping.data.order.model.dto.response.FixedDiscountPolicyResponse
import woowacourse.shopping.data.order.model.dto.response.OrderSummaryResponse
import woowacourse.shopping.data.order.model.toDomain
import woowacourse.shopping.data.util.RetrofitCallback

class OrderRemoteRepository(
    retrofit: Retrofit
) : OrderRepository {

    private val retrofitOrderService: RetrofitOrderService = retrofit.create()

    override fun requestFetchAllOrders(
        success: (List<OrderSummary>) -> Unit,
        failure: () -> Unit
    ) {
        val retrofitCallback = object : RetrofitCallback<List<OrderSummaryResponse>>() {
            override fun onSuccess(response: List<OrderSummaryResponse>?) {
                if (response == null) return
                success(response.map(OrderSummaryResponse::toDomain))
            }
        }
        retrofitOrderService.requestAllOrders().enqueue(retrofitCallback)
    }

    override fun requestAddOrder(
        cartIds: List<Long>,
        finalPrice: Int,
        success: (orderId: Long) -> Unit,
        failure: () -> Unit
    ) {
        val retrofitCallback = object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                val cartId = response.headers()["Location"]?.split("/")?.last()?.toLong() ?: return
                success(cartId)
            }
            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Log.e("Request Failed", t.toString())
            }
        }
        val orderRequest = OrderRequest(cartIds, finalPrice)
        retrofitOrderService.requestAddOrder(orderRequest).enqueue(retrofitCallback)
    }

    override fun requestFetchOrderById(
        id: Long,
        success: (order: Order?) -> Unit,
        failure: () -> Unit
    ) {
        val retrofitCallback = object : RetrofitCallback<Order>() {
            override fun onSuccess(response: Order?) {
                if (response == null) return
                success(response)
            }
        }
        retrofitOrderService.requestFetchOrderById(id).enqueue(retrofitCallback)
    }

    override fun requestFetchDiscountPolicy(
        success: (fixedDiscountPolicy: FixedDiscountPolicy) -> Unit,
        failure: () -> Unit
    ) {
        val retrofitCallback = object : RetrofitCallback<FixedDiscountPolicyResponse>() {
            override fun onSuccess(response: FixedDiscountPolicyResponse?) {
                if (response == null) return
                success(response.toDomain())
            }
        }
        retrofitOrderService.requestFetchDiscountPolicy().enqueue(retrofitCallback)
    }
}
