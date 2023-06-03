package woowacourse.shopping.data.cash

import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CashRemoteDataSource(
    baseUrl: String,
    private val userId: String,
) : CashDataSource {
    private val retrofitService: CashRetrofitService = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(CashRetrofitService::class.java)

    override fun loadCash(callback: (Int) -> Unit) {
        retrofitService.getCash(userId)
            .enqueue(
                object : retrofit2.Callback<TotalCash> {
                    override fun onResponse(
                        call: Call<TotalCash>,
                        response: Response<TotalCash>,
                    ) {
                        val totalCash = response.body()?.totalCash ?: 0
                        callback(totalCash)
                    }

                    override fun onFailure(call: Call<TotalCash>, t: Throwable) {}
                },
            )
    }

    override fun chargeCash(cash: Int, callback: (Int) -> Unit) {
        retrofitService.chargeCash(userId, cash)
            .enqueue(
                object : retrofit2.Callback<TotalCash> {
                    override fun onResponse(
                        call: Call<TotalCash>,
                        response: Response<TotalCash>,
                    ) {
                        val totalCash = response.body()?.totalCash ?: 0
                        callback(totalCash)
                    }

                    override fun onFailure(call: Call<TotalCash>, t: Throwable) {}
                },
            )
    }
}
