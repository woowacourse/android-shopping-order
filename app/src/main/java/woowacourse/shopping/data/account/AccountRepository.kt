package woowacourse.shopping.data.account

import woowacourse.shopping.data.carts.CartFetchResult

interface AccountRepository {
    suspend fun saveBasicKey(): Result<Unit>

    suspend fun checkValidBasicKey(basicKey: String): CartFetchResult<Int>

    suspend fun checkValidLocalSavedBasicKey(): CartFetchResult<Int>
}
