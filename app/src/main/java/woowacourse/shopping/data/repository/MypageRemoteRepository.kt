package woowacourse.shopping.data.repository

import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.data.retrofit.MypageApi
import woowacourse.shopping.domain.model.TotalCashDTO
import woowacourse.shopping.domain.repository.MypageRepository

class MypageRemoteRepository(baseUrl: String) : MypageRepository {
    private val retrofitService = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(MypageApi::class.java)

    override fun getCash(callback: (Int) -> Unit) {
        retrofitService.requestCash().enqueue(object : retrofit2.Callback<TotalCashDTO> {
            override fun onResponse(call: Call<TotalCashDTO>, response: Response<TotalCashDTO>) {
                response.body()?.let {
                    callback(it.totalCash)
                }
            }

            override fun onFailure(call: Call<TotalCashDTO>, t: Throwable) {
                throw t
            }
        })
    }

    override fun chargeCash(cash: Int, callback: (Int) -> Unit) {
        retrofitService.requestChargeCash(cash).enqueue(object : retrofit2.Callback<TotalCashDTO> {
            override fun onResponse(call: Call<TotalCashDTO>, response: Response<TotalCashDTO>) {
                response.body()?.let {
                    callback(it.totalCash)
                }
            }

            override fun onFailure(call: Call<TotalCashDTO>, t: Throwable) {
                throw t
            }
        })
    }
}
