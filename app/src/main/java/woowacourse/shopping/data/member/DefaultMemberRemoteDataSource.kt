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

    override fun getPoints(onSuccess: (Int) -> Unit, onFailure: (String) -> Unit) {
        memberService.requestPoints().enqueue(object : Callback<PointEntity> {
            override fun onResponse(call: Call<PointEntity>, response: Response<PointEntity>) {
                if(response.isSuccessful && response.body() != null) {
                    onSuccess(response.body()!!.points)
                }
                else {
                    onFailure(response.message().ifBlank { MESSAGE_GET_POINTS_FAILED })
                }
            }

            override fun onFailure(call: Call<PointEntity>, t: Throwable) {
                onFailure(MESSAGE_GET_POINTS_FAILED)
            }
        })
    }

    override fun getOrderHistories(onSuccess: (List<OrderHistory>) -> Unit, onFailure: (String) -> Unit) {
        memberService.requestOrderHistories().enqueue(object : Callback<List<OrderHistoryEntity>> {
            override fun onResponse(
                call: Call<List<OrderHistoryEntity>>,
                response: Response<List<OrderHistoryEntity>>
            ) {
                if(response.isSuccessful && response.body() != null) {
                    onSuccess(response.body()!!.map { it.toDomain() })
                }
                else {
                    onFailure(response.message().ifBlank { MESSAGE_GET_ORDER_HISTORIES_FAILED })
                }
            }

            override fun onFailure(call: Call<List<OrderHistoryEntity>>, t: Throwable) {
                onFailure(MESSAGE_GET_ORDER_HISTORIES_FAILED)
            }
        })
    }

    override fun getOrder(id: Int, onSuccess: (Order) -> Unit, onFailure: (String) -> Unit) {
        memberService.requestOrder(id).enqueue(object : Callback<OrderEntity> {
            override fun onResponse(call: Call<OrderEntity>, response: Response<OrderEntity>) {
                if(response.isSuccessful && response.body() != null) {
                    onSuccess(response.body()!!.toDomain())
                }
                else {
                    onFailure(response.message().ifBlank { MESSAGE_GET_ORDER_FAILED })
                }
            }

            override fun onFailure(call: Call<OrderEntity>, t: Throwable) {
                onFailure(MESSAGE_GET_ORDER_FAILED)
            }
        })
    }

    companion object {
        private const val MESSAGE_GET_POINTS_FAILED = "포인트를 불러오는데 실패했습니다."
        private const val MESSAGE_GET_ORDER_HISTORIES_FAILED = "주문 목록을 불러오는데 실패했습니다."
        private const val MESSAGE_GET_ORDER_FAILED = "주문 정보를 불러오는데 실패했습니다."
    }
}