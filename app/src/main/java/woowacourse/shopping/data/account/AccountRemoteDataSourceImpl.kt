package woowacourse.shopping.data.account

import woowacourse.shopping.data.util.NetworkModule
import woowacourse.shopping.data.util.RetrofitService
import woowacourse.shopping.data.util.api.ApiError
import woowacourse.shopping.data.util.api.ApiResult

class AccountRemoteDataSourceImpl(
    private val retrofitService: RetrofitService = NetworkModule.retrofitService,
) : AccountRemoteDataSource {
    override suspend fun fetchAuthCode(validKey: String): ApiResult<Int> {
        try {
            val response = retrofitService.requestCartCounts()
            return when {
                response.isSuccessful -> {
                    val authCode = response.code()
                    ApiResult.Success(authCode)
                }
                else -> ApiResult.Error(ApiError.Server(response.code(), response.message()))
            }
        } catch (e: Exception) {
            return ApiResult.Error(ApiError.Network)
        }
    }
}
