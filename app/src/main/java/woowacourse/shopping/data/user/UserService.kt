package woowacourse.shopping.data.user

import retrofit2.Call
import retrofit2.http.GET
import woowacourse.shopping.data.common.model.BaseResponse
import woowacourse.shopping.data.user.model.UserDataModel

interface UserService {
    @GET("members/point")
    fun getUserPoint(): Call<BaseResponse<UserDataModel>>
}
