package woowacourse.shopping.data.order

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import woowacourse.shopping.data.entity.CartItemIdsEntity
import woowacourse.shopping.data.entity.DiscountEntity.Companion.toDomain
import woowacourse.shopping.data.entity.DiscountsEntity
import woowacourse.shopping.data.entity.OrderEntity
import woowacourse.shopping.data.entity.OrderEntity.Companion.toDomain
import woowacourse.shopping.domain.order.Order
import woowacourse.shopping.domain.order.Payment
import woowacourse.shopping.domain.user.User

class OrderRemoteSource(retrofit: Retrofit) : OrderDataSource {
    private val orderService = retrofit.create(OrderRetrofitService::class.java)

    override fun save(cartItemIds: List<Long>, user: User, onFinish: (Long) -> Unit) {
        orderService.postOrder("Basic " + user.token, CartItemIdsEntity(cartItemIds))
            .enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (response.code() != 201) return
                    onFinish(
                        response.headers()["Location"]?.substringAfterLast("/")?.toLong() ?: return
                    )
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    println(t.stackTraceToString())
                    println(t.message)
                }
            })
    }

    override fun findById(id: Long, user: User, onFinish: (Order) -> Unit) {
        orderService.selectOrderById("Basic " + user.token, id)
            .enqueue(object : Callback<OrderEntity> {
                override fun onResponse(call: Call<OrderEntity>, response: Response<OrderEntity>) {
                    if (response.code() != 200) return
                    onFinish(response.body()?.toDomain() ?: return)
                }

                override fun onFailure(call: Call<OrderEntity>, t: Throwable) {
                    println(t.stackTraceToString())
                    println(t.message)
                }
            })
    }

    override fun findAll(user: User, onFinish: (List<Order>) -> Unit) {
        orderService.selectOrders("Basic " + user.token)
            .enqueue(object : Callback<List<OrderEntity>> {
                override fun onResponse(
                    call: Call<List<OrderEntity>>,
                    response: Response<List<OrderEntity>>
                ) {
                    if (response.code() != 200) return
                    onFinish(response.body()?.map { it.toDomain() } ?: return)
                }

                override fun onFailure(call: Call<List<OrderEntity>>, t: Throwable) {
                    println(t.stackTraceToString())
                    println(t.message)
                }
            })
    }

    override fun findDiscountPolicy(price: Int, memberGrade: String, onFinish: (Payment) -> Unit) {
        orderService.selectDiscountPolicy(price, memberGrade)
            .enqueue(object : Callback<DiscountsEntity> {
                override fun onResponse(
                    call: Call<DiscountsEntity>,
                    response: Response<DiscountsEntity>
                ) {
                    if (response.code() != 200) return
                    onFinish(
                        Payment(
                            response.body()?.discountInformation?.map { it.toDomain() }
                                ?: return
                        )
                    )
                }

                override fun onFailure(call: Call<DiscountsEntity>, t: Throwable) {
                    println(t.stackTraceToString())
                    println(t.message)
                }
            })
    }
}
