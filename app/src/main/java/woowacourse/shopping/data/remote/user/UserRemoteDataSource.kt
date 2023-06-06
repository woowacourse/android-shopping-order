package woowacourse.shopping.data.remote.user

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.ApiClient
import woowacourse.shopping.data.common.BaseResponse
import woowacourse.shopping.data.common.SharedPreferencesDb

class UserRemoteDataSource(private val sharedPreferences: SharedPreferencesDb) : UserDataSource {
    private val userClient = ApiClient.client.create(UserService::class.java)
    override fun getPoint(onSuccess: (BaseResponse<PointResponse>) -> Unit, onFailure: () -> Unit) {
        userClient.getPoint().enqueue(object : Callback<BaseResponse<PointResponse>> {
            override fun onResponse(
                call: Call<BaseResponse<PointResponse>>,
                response: Response<BaseResponse<PointResponse>>
            ) {
                if (response.body() == null) {
                    onFailure(call, Throwable())
                    return
                }
                onSuccess(response.body()!!)
            }

            override fun onFailure(call: Call<BaseResponse<PointResponse>>, t: Throwable) {
                onFailure()
            }
        })
    }
}
