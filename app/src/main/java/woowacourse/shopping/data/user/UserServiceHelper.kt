package woowacourse.shopping.data.user

import retrofit2.Call
import woowacourse.shopping.data.ApiClient
import woowacourse.shopping.data.common.SharedPreferencesDb
import woowacourse.shopping.data.common.model.BaseResponse
import woowacourse.shopping.data.user.model.UserDataModel
import woowacourse.shopping.presentation.serversetting.ServerSettingPresenter

class UserServiceHelper(private val sharedPreferences: SharedPreferencesDb) : UserRemoteDataSource {
    private val userService = ApiClient.client
        .create(UserService::class.java)

    private fun getAuthToken() =
        sharedPreferences.getString(ServerSettingPresenter.AUTHORIZATION_TOKEN, "")

    override fun getUserPoint(): Call<BaseResponse<UserDataModel>> {
        return userService.getUserPoint(credentials = getAuthToken())
    }
}
