package woowacourse.shopping.data.datasource.user

import android.util.Log
import retrofit2.Call
import retrofit2.Response
import woowacourse.shopping.data.NetworkModule
import woowacourse.shopping.data.datasource.request.UserRequest
import woowacourse.shopping.data.remote.OkHttpModule

class UserRemoteDataSourceImpl : UserRemoteDataSource {

    private val userService = NetworkModule.getService<UserService>()

    override fun getUser(onReceived: (user: UserRequest) -> Unit) {
        userService.getUser(
            authorization = OkHttpModule.AUTHORIZATION_FORMAT.format(OkHttpModule.encodedUserInfo)
        ).enqueue(object : retrofit2.Callback<UserRequest> {
            override fun onResponse(call: Call<UserRequest>, response: Response<UserRequest>) {
                Log.d("woogi", "onResponse: ${response.body()}")
                response.body()?.let {
                    onReceived(it)
                }
            }

            override fun onFailure(call: Call<UserRequest>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }
}
