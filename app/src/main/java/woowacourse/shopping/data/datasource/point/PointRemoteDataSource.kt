package woowacourse.shopping.data.datasource.point

import android.util.Log
import retrofit2.Call
import retrofit2.Response
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.data.dto.PointDto
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.data.util.retrofit.RetrofitUtil
import woowacourse.shopping.domain.model.Point

class PointRemoteDataSource : PointDataSource {
    private val baseUrl: String = ShoppingApplication.pref.getBaseUrl().toString()
    private val pointService = RetrofitUtil.getPointByRetrofit(baseUrl)

    override fun requestPoints(
        token: String,
        onSuccess: (Point) -> Unit,
        onFailure: (String) -> Unit,
    ) {
        val call = pointService.requestPoints(token)
        call.enqueue(object : retrofit2.Callback<PointDto> {
            override fun onResponse(call: Call<PointDto>, response: Response<PointDto>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    Log.d("test", "retrofit requestPoints result $result")
                    if (result != null) {
                        onSuccess(result.toDomain())
                    }
                } else {
                    Log.d("test", "retrofit 실패")
                }
            }

            override fun onFailure(call: Call<PointDto>, t: Throwable) {
                onFailure(t.message.toString())
            }
        })
    }
}
