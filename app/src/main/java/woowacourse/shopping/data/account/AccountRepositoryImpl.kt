package woowacourse.shopping.data.account

import woowacourse.shopping.data.util.api.ApiError
import woowacourse.shopping.data.util.api.ApiResult
import woowacourse.shopping.domain.model.Authorization

class AccountRepositoryImpl(
    private val remoteDataSource: AccountRemoteDataSource,
    private val accountLocalDataSource: AccountLocalDataSource,
) : AccountRepository {
    override suspend fun saveBasicKey(): Result<Unit> = accountLocalDataSource.saveBasicKey(Authorization.basicKey)

    override suspend fun checkValidBasicKey(basicKey: String): ApiResult<Int> = remoteDataSource.fetchAuthCode(basicKey)

    override suspend fun checkValidLocalSavedBasicKey(): ApiResult<Int> {
        val result = accountLocalDataSource.loadBasicKey()
        when {
            result.isSuccess -> {
                val basicKey = result.getOrDefault("")
                if (basicKey.isNotEmpty()) {
                    Authorization.setBasicKey(basicKey)
                    return remoteDataSource.fetchAuthCode(basicKey)
                }
            }
        }
        return ApiResult.Error(ApiError.Local)
    }
}
