package woowacourse.shopping.data.user

import retrofit2.Call
import retrofit2.http.GET
import woowacourse.shopping.data.entity.UserEntity

interface UserRetrofitService {
    @GET("/members")
    fun selectUsers(): Call<List<UserEntity>>
}
