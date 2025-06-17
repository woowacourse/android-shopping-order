package woowacourse.shopping.data.account

import woowacourse.shopping.data.util.api.ApiResult

interface AccountRemoteDataSource {
    suspend fun fetchAuthCode(validKey: String): ApiResult<Int>
}
