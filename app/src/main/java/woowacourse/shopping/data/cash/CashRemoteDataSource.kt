package woowacourse.shopping.data.cash

import retrofit2.Retrofit
import woowacourse.shopping.data.util.RetrofitCallback

class CashRemoteDataSource(
    retrofit: Retrofit,
) : CashDataSource {
    private val retrofitService: CashRetrofitService =
        retrofit.create(CashRetrofitService::class.java)

    override fun loadCash(callback: (Int) -> Unit) {
        retrofitService.getCash()
            .enqueue(
                object : RetrofitCallback<TotalCash>() {
                    override fun onSuccess(response: TotalCash?) {
                        val totalCash = response?.totalCash ?: 0
                        callback(totalCash)
                    }
                },
            )
    }

    override fun chargeCash(cash: Int, callback: (Int) -> Unit) {
        retrofitService.chargeCash(cash)
            .enqueue(
                object : RetrofitCallback<TotalCash>() {
                    override fun onSuccess(response: TotalCash?) {
                        val totalCash = response?.totalCash ?: 0
                        callback(totalCash)
                    }
                },
            )
    }
}
