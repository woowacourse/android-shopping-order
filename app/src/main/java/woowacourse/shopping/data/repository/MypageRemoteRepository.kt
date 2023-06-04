package woowacourse.shopping.data.repository

import retrofit2.Call
import retrofit2.Response
import woowacourse.shopping.data.retrofit.MypageApi
import woowacourse.shopping.data.retrofit.RetrofitGenerator
import woowacourse.shopping.domain.model.TotalCashDTO
import woowacourse.shopping.domain.repository.MypageRepository
import woowacourse.shopping.domain.repository.ServerStoreRespository

class MypageRemoteRepository(
    serverRepository: ServerStoreRespository,
) : MypageRepository {
    private val retrofitService =
        RetrofitGenerator.create(serverRepository.getServerUrl(), MypageApi::class.java)

    override fun getCash(callback: (Int) -> Unit) {
        retrofitService.requestCash().enqueue(object : retrofit2.Callback<TotalCashDTO> {
            override fun onResponse(call: Call<TotalCashDTO>, response: Response<TotalCashDTO>) {
                response.body()?.let {
                    if (!response.isSuccessful) {
                        onFailure(call, Throwable(SERVER_ERROR_MESSAGE))
                        return
                    }
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
                    if (!response.isSuccessful) {
                        onFailure(call, Throwable(SERVER_ERROR_MESSAGE))
                        return
                    }
                    callback(it.totalCash)
                }
            }

            override fun onFailure(call: Call<TotalCashDTO>, t: Throwable) {
                throw t
            }
        })
    }

    companion object {
        private const val SERVER_ERROR_MESSAGE = "서버와의 통신이 원활하지 않습니다."
    }
}
