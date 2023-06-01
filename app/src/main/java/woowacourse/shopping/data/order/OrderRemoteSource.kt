package woowacourse.shopping.data.order

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.entity.CartItemIdsEntity
import woowacourse.shopping.data.entity.DiscountsEntity
import woowacourse.shopping.data.entity.OrderEntity
import woowacourse.shopping.network.RetrofitErrorHandlerProvider
import woowacourse.shopping.network.retrofit.OrderRetrofitService

class OrderRemoteSource(private val orderService: OrderRetrofitService) : OrderDataSource {
    override fun save(
        cartItemIds: List<Long>,
        userToken: String,
        onFinish: (Result<Long>) -> Unit
    ) {
        orderService.postOrder("Basic $userToken", CartItemIdsEntity(cartItemIds))
            .enqueue(object : Callback<Unit> {
                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    onFinish(Result.failure(t))
                }

                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (response.code() != 201) return onFinish(Result.failure(Throwable(response.message())))
                    val id = response.headers()["Location"]?.substringAfterLast("/")?.toLong()
                        ?: return onFinish(Result.failure(Throwable(response.message())))
                    onFinish(Result.success(id))
                }
            })
    }

    override fun findById(id: Long, userToken: String, onFinish: (Result<OrderEntity>) -> Unit) {
        orderService.selectOrderById("Basic $userToken", id)
            .enqueue(RetrofitErrorHandlerProvider.callbackWithBody(200, onFinish))
    }

    override fun findAll(userToken: String, onFinish: (Result<List<OrderEntity>>) -> Unit) {
        orderService.selectOrders("Basic $userToken")
            .enqueue(RetrofitErrorHandlerProvider.callbackWithBody(200, onFinish))
    }

    override fun findDiscountPolicy(
        price: Int,
        memberGrade: String,
        onFinish: (Result<DiscountsEntity>) -> Unit
    ) {
        orderService.selectDiscountPolicy(price, memberGrade)
            .enqueue(RetrofitErrorHandlerProvider.callbackWithBody(200, onFinish))
    }
}
