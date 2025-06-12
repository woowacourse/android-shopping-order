package woowacourse.shopping.data.account

import woowacourse.shopping.data.carts.CartFetchError
import woowacourse.shopping.data.carts.CartFetchResult
import woowacourse.shopping.domain.model.Authorization

class AccountRepositoryImpl(
    private val remoteDataSource: AccountRemoteDataSource,
    private val accountLocalDataSource: AccountLocalDataSource,
) : AccountRepository {
    override suspend fun saveBasicKey(): Result<Unit> = accountLocalDataSource.saveBasicKey(Authorization.basicKey)

    override suspend fun checkValidBasicKey(basicKey: String): CartFetchResult<Int> = remoteDataSource.fetchAuthCode(basicKey)

    override suspend fun checkValidLocalSavedBasicKey(): CartFetchResult<Int> {
        val result = accountLocalDataSource.loadBasicKey()
        when {
            result.isSuccess -> {
                val basicKey = result.getOrNull() ?: ""
                if (basicKey.isNotEmpty()) {
                    Authorization.setBasicKey(basicKey)
                    return remoteDataSource.fetchAuthCode(basicKey)
                }
            }
        }
        return CartFetchResult.Error(CartFetchError.Local)
    }
}
