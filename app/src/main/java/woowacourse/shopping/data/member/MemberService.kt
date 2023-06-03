package woowacourse.shopping.data.member

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import woowacourse.shopping.data.entity.PointEntity

interface MemberService {
    @GET("members/points")
    fun requestPoints(
        @Header("Authorization") authorization: String
    ) : Call<PointEntity>
}