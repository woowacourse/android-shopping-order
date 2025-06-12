package woowacourse.shopping.data.account

import woowacourse.shopping.data.carts.CartFetchResult

interface AccountRemoteDataSource {
    suspend fun fetchAuthCode(validKey: String): CartFetchResult<Int>
}
