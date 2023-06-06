package woowacourse.shopping.data.cash

import android.util.Log
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit

class CashRemoteDataSource(
    retrofit: Retrofit,
) : CashDataSource {
    private val retrofitService: CashRetrofitService =
        retrofit.create(CashRetrofitService::class.java)

    override fun loadCash(callback: (Int) -> Unit) {
        retrofitService.getCash()
            .enqueue(
                object : retrofit2.Callback<TotalCash> {
                    override fun onResponse(
                        call: Call<TotalCash>,
                        response: Response<TotalCash>,
                    ) {
                        val totalCash = response.body()?.totalCash ?: 0
                        callback(totalCash)
                    }

                    override fun onFailure(call: Call<TotalCash>, t: Throwable) {
                        Log.e("Request Failed", t.toString())
                    }
                },
            )
    }

    override fun chargeCash(cash: Int, callback: (Int) -> Unit) {
        retrofitService.chargeCash(cash)
            .enqueue(
                object : retrofit2.Callback<TotalCash> {
                    override fun onResponse(
                        call: Call<TotalCash>,
                        response: Response<TotalCash>,
                    ) {
                        val totalCash = response.body()?.totalCash ?: 0
                        callback(totalCash)
                    }

                    override fun onFailure(call: Call<TotalCash>, t: Throwable) {
                        Log.e("Request Failed", t.toString())
                    }
                },
            )
    }
}
