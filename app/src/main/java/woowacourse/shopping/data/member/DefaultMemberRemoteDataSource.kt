package woowacourse.shopping.data.member

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import woowacourse.shopping.data.entity.OrderEntity
import woowacourse.shopping.data.entity.OrderHistoryEntity
import woowacourse.shopping.data.entity.PointEntity
import woowacourse.shopping.data.entity.mapper.OrderHistoryMapper.toDomain
import woowacourse.shopping.data.entity.mapper.OrderMapper.toDomain
import woowacourse.shopping.data.server.MemberRemoteDataSource
import woowacourse.shopping.domain.Order
import woowacourse.shopping.domain.OrderHistory

class DefaultMemberRemoteDataSource(retrofit: Retrofit) : MemberRemoteDataSource {
    private val memberService: MemberService = retrofit.create(MemberService::class.java)

    override fun getPoints(onSuccess: (Int) -> Unit, onFailure: () -> Unit) {
        memberService.requestPoints().enqueue(object : Callback<PointEntity> {
            override fun onResponse(call: Call<PointEntity>, response: Response<PointEntity>) {
                if(response.isSuccessful && response.body() != null) {
                    onSuccess(response.body()!!.points)
                }
                else {
                    onFailure()
                }
            }

            override fun onFailure(call: Call<PointEntity>, t: Throwable) {
                onFailure()
            }
        })
    }

    override fun getOrderHistories(onSuccess: (List<OrderHistory>) -> Unit, onFailure: () -> Unit) {
        memberService.requestOrderHistories().enqueue(object : Callback<List<OrderHistoryEntity>> {
            override fun onResponse(
                call: Call<List<OrderHistoryEntity>>,
                response: Response<List<OrderHistoryEntity>>
            ) {
                if(response.isSuccessful && response.body() != null) {
                    onSuccess(response.body()!!.map { it.toDomain() })
                }
                else {
                    onFailure()
                }
            }

            override fun onFailure(call: Call<List<OrderHistoryEntity>>, t: Throwable) {
                onFailure()
            }
        })
    }

    override fun getOrder(id: Int, onSuccess: (Order) -> Unit, onFailure: () -> Unit) {
        memberService.requestOrder(id).enqueue(object : Callback<OrderEntity> {
            override fun onResponse(call: Call<OrderEntity>, response: Response<OrderEntity>) {
                if(response.isSuccessful && response.body() != null) {
                    onSuccess(response.body()!!.toDomain())
                }
                else {
                    onFailure()
                }
            }

            override fun onFailure(call: Call<OrderEntity>, t: Throwable) {
                onFailure()
            }
        })
    }
}