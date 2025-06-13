package woowacourse.shopping.data.account

import woowacourse.shopping.data.util.api.ApiResult
import woowacourse.shopping.domain.model.Authorization

class AccountRepositoryImpl(
    private val remoteDataSource: AccountRemoteDataSource,
    private val accountLocalDataSource: AccountLocalDataSource,
) : AccountRepository {
    override suspend fun saveBasicKey(): Result<Unit> = accountLocalDataSource.saveBasicKey(Authorization.basicKey)

    override suspend fun checkValidBasicKey(basicKey: String): BasicKeyAuthorizationResult {
        val result = remoteDataSource.fetchAuthCode(basicKey)
        return when (result) {
            is ApiResult.Error -> BasicKeyAuthorizationResult.LoginError
            is ApiResult.Success -> {
                when (result.data) {
                    200 -> BasicKeyAuthorizationResult.LoginSuccess
                    401, 403 -> BasicKeyAuthorizationResult.LoginError
                    else -> BasicKeyAuthorizationResult.LoginError
                }
            }
        }
    }

    override suspend fun checkValidLocalSavedBasicKey(): BasicKeyAuthorizationResult {
        val result = accountLocalDataSource.loadBasicKey()
        when {
            result.isSuccess -> {
                val basicKey = result.getOrDefault("")
                if (basicKey.isNotEmpty()) {
                    Authorization.setBasicKey(basicKey)
                    return checkValidBasicKey(basicKey)
                }
            }
        }
        return BasicKeyAuthorizationResult.LoginError
    }
}
