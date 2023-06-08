package woowacourse.shopping.data.cash

import retrofit2.Retrofit
import woowacourse.shopping.data.util.RetrofitCallback

class CashRemoteDataSource(
    retrofit: Retrofit,
) : CashDataSource {
    private val retrofitService: CashRetrofitService =
        retrofit.create(CashRetrofitService::class.java)

    override fun loadCash(callback: (Int) -> Unit) {
        val retrofitCallback = object : RetrofitCallback<TotalCash>() {
            override fun onSuccess(response: TotalCash?) {
                val totalCash = response?.totalCash ?: 0
                callback(totalCash)
            }
        }
        retrofitService.getCash().enqueue(retrofitCallback)
    }

    override fun chargeCash(cash: Int, callback: (Int) -> Unit) {
        val retrofitCallback = object : RetrofitCallback<TotalCash>() {
            override fun onSuccess(response: TotalCash?) {
                val totalCash = response?.totalCash ?: 0
                callback(totalCash)
            }
        }
        retrofitService.chargeCash(cash).enqueue(retrofitCallback)
    }
}
