package woowacourse.shopping.data.datasource.userpointdata.remote

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.datasource.userpointdata.UserPointInfoDataSource
import woowacourse.shopping.data.model.DataUserPointInfo
import woowacourse.shopping.data.remote.RetrofitModule
import woowacourse.shopping.data.remote.mapper.toDataModel
import woowacourse.shopping.data.remote.response.UserPointInfoResponse

class RemoteUserPointInfoDataSource : UserPointInfoDataSource.Remote {
    override fun getUserPointInfo(onReceived: (DataUserPointInfo) -> Unit) {
        RetrofitModule.userPointInfoService.getUserPointInfo().enqueue(
            object : Callback<UserPointInfoResponse> {
                override fun onResponse(
                    call: Call<UserPointInfoResponse>,
                    response: Response<UserPointInfoResponse>
                ) {
                    val body = response.body()
                    if (body != null) {
                        onReceived(body.toDataModel())
                    } else {
                        Log.i(
                            this@RemoteUserPointInfoDataSource.javaClass.name,
                            NULL_USER_POINT_DATA
                        )
                    }
                }

                override fun onFailure(call: Call<UserPointInfoResponse>, t: Throwable) {}
            }
        )
    }

    companion object {
        private const val NULL_USER_POINT_DATA = "서버에서 넘어오는 유저 포인트 데이터가 null 입니다."
    }
}
