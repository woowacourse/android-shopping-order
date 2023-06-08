package woowacourse.shopping.data.datasource.userpointdata.remote

import android.util.Log
import retrofit2.Response
import woowacourse.shopping.data.datasource.userpointdata.UserPointInfoDataSource
import woowacourse.shopping.data.httpclient.RetrofitModule
import woowacourse.shopping.data.httpclient.getRetrofitCallback
import woowacourse.shopping.data.httpclient.mapper.toDataModel
import woowacourse.shopping.data.httpclient.response.UserPointInfoResponse
import woowacourse.shopping.data.model.DataUserPointInfo

class RemoteUserPointInfoDataSource : UserPointInfoDataSource.Remote {
    override fun getUserPointInfo(onReceived: (DataUserPointInfo) -> Unit) {
        RetrofitModule.userPointInfoService.getUserPointInfo().enqueue(
            getRetrofitCallback<UserPointInfoResponse>(
                failureLogTag = this::class.java.name,
                onResponse = { _, response -> getUserPointInfoOnResponse(response, onReceived) }
            )
        )
    }

    private fun getUserPointInfoOnResponse(
        response: Response<UserPointInfoResponse>,
        onReceived: (DataUserPointInfo) -> Unit
    ) {
        val body = response.body()
        if (body != null) {
            onReceived(body.toDataModel())
        } else {
            Log.i(this.javaClass.name, NULL_USER_POINT_DATA)
        }
    }

    companion object {
        private const val NULL_USER_POINT_DATA = "서버에서 넘어오는 유저 포인트 데이터가 null 입니다."
    }
}
