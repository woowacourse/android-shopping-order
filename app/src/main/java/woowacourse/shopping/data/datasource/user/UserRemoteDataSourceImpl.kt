package woowacourse.shopping.data.datasource.user

import retrofit2.Call
import retrofit2.Response
import woowacourse.shopping.data.NetworkModule.AUTHORIZATION_FORMAT
import woowacourse.shopping.data.NetworkModule.encodedUserInfo
import woowacourse.shopping.data.NetworkModule.userService
import woowacourse.shopping.data.datasource.response.UserEntity

class UserRemoteDataSourceImpl : UserRemoteDataSource {

    override fun getUser(
        onReceived: (user: UserEntity) -> Unit,
        onFailure: (errorMessage: String) -> Unit,
    ) {
        userService.getUser(
            authorization = AUTHORIZATION_FORMAT.format(encodedUserInfo)
        ).enqueue(object : retrofit2.Callback<UserEntity> {

            override fun onResponse(call: Call<UserEntity>, response: Response<UserEntity>) {
                response.body()?.let {
                    onReceived(it)
                } ?: onFailure(FAILED_TO_GET_USER_INFO)
            }

            override fun onFailure(call: Call<UserEntity>, t: Throwable) {
                onFailure(FAILED_TO_GET_USER_INFO)
            }
        })
    }

    companion object {
        private const val FAILED_TO_GET_USER_INFO = "유저 정보를 불러올 수 없습니다"
    }
}
