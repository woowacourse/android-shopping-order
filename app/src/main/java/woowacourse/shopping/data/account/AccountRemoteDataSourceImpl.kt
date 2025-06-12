package woowacourse.shopping.data.account

import woowacourse.shopping.data.carts.CartFetchError
import woowacourse.shopping.data.carts.CartFetchResult
import woowacourse.shopping.data.util.NetworkModule
import woowacourse.shopping.data.util.RetrofitService

class AccountRemoteDataSourceImpl(
    private val retrofitService: RetrofitService = NetworkModule.retrofitService,
) : AccountRemoteDataSource {
    override suspend fun fetchAuthCode(validKey: String): CartFetchResult<Int> {
        try {
            val response = retrofitService.requestCartCounts()
            return when {
                response.isSuccessful -> CartFetchResult.Success(response.code())
                else -> CartFetchResult.Error(CartFetchError.Server(response.code(), response.message()))
            }
        } catch (e: Exception) {
            return CartFetchResult.Error(CartFetchError.Network)
        }
    }
}
