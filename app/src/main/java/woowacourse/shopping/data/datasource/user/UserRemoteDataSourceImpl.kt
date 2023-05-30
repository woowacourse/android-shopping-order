package woowacourse.shopping.data.datasource.user

import android.util.Log
import retrofit2.Call
import retrofit2.Response
import woowacourse.shopping.data.NetworkModule
import woowacourse.shopping.data.model.DataUser
import woowacourse.shopping.data.remote.OkHttpModule

class UserRemoteDataSourceImpl : UserRemoteDataSource {

    private val userService = NetworkModule.getService<UserService>()

    override fun getUser(onReceived: (user: DataUser) -> Unit) {
        userService.getUser(
            authorization = OkHttpModule.AUTHORIZATION_FORMAT.format(OkHttpModule.encodedUserInfo)
        ).enqueue(object : retrofit2.Callback<DataUser> {
            override fun onResponse(call: Call<DataUser>, response: Response<DataUser>) {
                Log.d("woogi", "onResponse: ${response.body()}")
                response.body()?.let {
                    onReceived(it)
                }
            }

            override fun onFailure(call: Call<DataUser>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }
}
