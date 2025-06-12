package woowacourse.shopping.data.account

import woowacourse.shopping.data.util.api.ApiResult

interface AccountRepository {
    suspend fun saveBasicKey(): Result<Unit>

    suspend fun checkValidBasicKey(basicKey: String): ApiResult<Int>

    suspend fun checkValidLocalSavedBasicKey(): ApiResult<Int>
}
