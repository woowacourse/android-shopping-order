package woowacourse.shopping.data.repository.impl

import retrofit2.Call
import retrofit2.Response
import woowacourse.shopping.data.remote.MypageApi
import woowacourse.shopping.data.remote.RetrofitGenerator
import woowacourse.shopping.data.remote.dto.TotalCashDTO
import woowacourse.shopping.data.remote.result.DataResult
import woowacourse.shopping.data.repository.MypageRepository
import woowacourse.shopping.data.repository.ServerStoreRespository

class MypageRemoteRepository(
    serverRepository: ServerStoreRespository,
) : MypageRepository {
    private val retrofitService =
        RetrofitGenerator.create(serverRepository.getServerUrl(), MypageApi::class.java)

    override fun getCash(callback: (DataResult<Int>) -> Unit) {
        retrofitService.requestCash().enqueue(object : retrofit2.Callback<TotalCashDTO> {
            override fun onResponse(call: Call<TotalCashDTO>, response: Response<TotalCashDTO>) {
                response.body()?.let {
                    if (!response.isSuccessful) {
                        callback(DataResult.NotSuccessfulError)
                        return
                    }
                    callback(DataResult.Success(it.totalCash))
                }
            }

            override fun onFailure(call: Call<TotalCashDTO>, t: Throwable) {
                callback(DataResult.Failure)
            }
        })
    }

    override fun chargeCash(cash: Int, callback: (DataResult<Int>) -> Unit) {
        retrofitService.requestChargeCash(cash).enqueue(object : retrofit2.Callback<TotalCashDTO> {
            override fun onResponse(call: Call<TotalCashDTO>, response: Response<TotalCashDTO>) {
                response.body()?.let {
                    if (!response.isSuccessful) {
                        callback(DataResult.NotSuccessfulError)
                        return
                    }
                    callback(DataResult.Success(it.totalCash))
                }
            }

            override fun onFailure(call: Call<TotalCashDTO>, t: Throwable) {
                callback(DataResult.Failure)
            }
        })
    }
}
