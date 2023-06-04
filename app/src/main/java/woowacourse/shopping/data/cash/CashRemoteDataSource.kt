package woowacourse.shopping.data.cash

import android.util.Log
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit

class CashRemoteDataSource(
    baseUrl: String,
    private val userId: String,
) : CashDataSource {
    private val retrofitService: CashRetrofitService = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
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

                    override fun onFailure(call: Call<TotalCash>, t: Throwable) {
                        Log.e("Request Failed", t.toString())
                    }
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

                    override fun onFailure(call: Call<TotalCash>, t: Throwable) {
                        Log.e("Request Failed", t.toString())
                    }
                },
            )
    }
}
