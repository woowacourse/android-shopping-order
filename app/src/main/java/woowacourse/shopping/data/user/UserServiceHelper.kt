package woowacourse.shopping.data.user

import retrofit2.Call
import woowacourse.shopping.data.ApiClient
import woowacourse.shopping.data.common.model.BaseResponse
import woowacourse.shopping.data.user.model.UserDataModel

class UserServiceHelper() : UserRemoteDataSource {
    private val userService = ApiClient.client
        .create(UserService::class.java)
    override fun getUserPoint(): Call<BaseResponse<UserDataModel>> {
        return userService.getUserPoint()
    }
}
