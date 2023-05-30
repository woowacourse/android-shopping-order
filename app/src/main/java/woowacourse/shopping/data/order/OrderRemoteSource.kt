package woowacourse.shopping.data.order

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import woowacourse.shopping.data.entity.CartItemIdsEntity
import woowacourse.shopping.data.entity.DiscountEntity.Companion.toDomain
import woowacourse.shopping.data.entity.DiscountsEntity
import woowacourse.shopping.data.entity.OrderEntity
import woowacourse.shopping.data.entity.OrderEntity.Companion.toDomain
import woowacourse.shopping.domain.order.Order
import woowacourse.shopping.domain.order.Payment
import woowacourse.shopping.session.UserData
import woowacourse.shopping.utils.RemoteHost

class OrderRemoteSource(host: RemoteHost) : OrderDataSource {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    private val retrofitService = Retrofit.Builder().baseUrl(host.url)
        .addConverterFactory(MoshiConverterFactory.create(moshi)).build()
        .create(OrderRetrofitService::class.java)

    override fun save(cartItemIds: List<Long>, onFinish: (Long) -> Unit) {
        retrofitService.postOrder("Basic " + UserData.credential, CartItemIdsEntity(cartItemIds))
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

    override fun findById(id: Long, onFinish: (Order) -> Unit) {
        retrofitService.selectOrderById("Basic " + UserData.credential, id)
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

    override fun findAll(onFinish: (List<Order>) -> Unit) {
        retrofitService.selectOrders("Basic " + UserData.credential)
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
        retrofitService.selectDiscountPolicy(price, memberGrade)
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
