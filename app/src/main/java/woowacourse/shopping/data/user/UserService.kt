package woowacourse.shopping.data.user

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import woowacourse.shopping.data.common.model.BaseResponse
import woowacourse.shopping.data.user.model.UserDataModel

interface UserService {
    @GET("members/point")
    fun getUserPoint(
        @Header("Authorization") credentials: String,
    ): Call<BaseResponse<UserDataModel>>
}
