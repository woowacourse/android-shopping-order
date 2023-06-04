package woowacourse.shopping.data.member

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import woowacourse.shopping.Storage
import woowacourse.shopping.data.entity.OrderHistoryEntity
import woowacourse.shopping.data.entity.PointEntity
import woowacourse.shopping.data.entity.mapper.OrderHistoryMapper.toDomain
import woowacourse.shopping.data.server.MemberRemoteDataSource
import woowacourse.shopping.data.server.Server
import woowacourse.shopping.domain.OrderHistory

class MemberRemoteDataSourceRetrofit : MemberRemoteDataSource {
    private val memberService: MemberService = Retrofit.Builder()
        .baseUrl(Server.getUrl(Storage.server))
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(MemberService::class.java)

    override fun getPoints(onSuccess: (Int) -> Unit, onFailure: () -> Unit) {
        memberService.requestPoints(Storage.credential).enqueue(object : Callback<PointEntity> {
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

    override fun getHistories(onSuccess: (List<OrderHistory>) -> Unit, onFailure: () -> Unit) {
        memberService.requestHistories(Storage.credential).enqueue(object : Callback<List<OrderHistoryEntity>> {
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
}