package woowacourse.shopping.data.datasource.impl

import retrofit2.Call
import retrofit2.Response
import woowacourse.shopping.data.datasource.MypageDataSource
import woowacourse.shopping.data.remote.MypageApi
import woowacourse.shopping.data.remote.RetrofitGenerator
import woowacourse.shopping.data.remote.dto.TotalCashDTO
import woowacourse.shopping.data.remote.result.DataResult

class MypageRemoteDataSource(url: String) : MypageDataSource {
    private val mypageService =
        RetrofitGenerator.create(url, MypageApi::class.java)

    override fun getCash(callback: (DataResult<Int>) -> Unit) {
        mypageService.requestCash().enqueue(object : retrofit2.Callback<TotalCashDTO> {
            override fun onResponse(call: Call<TotalCashDTO>, response: Response<TotalCashDTO>) {
                response.body()?.let {
                    if (!response.isSuccessful) {
                        callback(DataResult.NotSuccessfulError)
                        return
                    }
                    if (!it.isNotNull) callback(DataResult.WrongResponse)
                    callback(DataResult.Success(it.totalCash ?: 0))
                }
            }

            override fun onFailure(call: Call<TotalCashDTO>, t: Throwable) {
                callback(DataResult.Failure)
            }
        })
    }

    override fun chargeCash(cash: Int, callback: (DataResult<Int>) -> Unit) {
        mypageService.requestChargeCash(cash).enqueue(object : retrofit2.Callback<TotalCashDTO> {
            override fun onResponse(call: Call<TotalCashDTO>, response: Response<TotalCashDTO>) {
                response.body()?.let {
                    if (!response.isSuccessful) {
                        callback(DataResult.NotSuccessfulError)
                        return
                    }
                    if (!it.isNotNull) {
                        callback(DataResult.WrongResponse)
                        return
                    }
                    callback(DataResult.Success(it.totalCash ?: 0))
                }
            }

            override fun onFailure(call: Call<TotalCashDTO>, t: Throwable) {
                callback(DataResult.Failure)
            }
        })
    }
}
