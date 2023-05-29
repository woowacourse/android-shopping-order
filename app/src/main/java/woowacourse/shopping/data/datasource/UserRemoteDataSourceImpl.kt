package woowacourse.shopping.data.datasource

import android.util.Log
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.data.NullOnEmptyConvertFactory
import woowacourse.shopping.data.model.DataUser
import woowacourse.shopping.data.remote.OkHttpModule

class UserRemoteDataSourceImpl : UserRemoteDataSource {

    private val url = OkHttpModule.BASE_URL
    private val userService = Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(NullOnEmptyConvertFactory)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(UserService::class.java)

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
