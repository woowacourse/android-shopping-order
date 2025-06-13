package woowacourse.shopping.data.account

interface AccountRepository {
    suspend fun saveBasicKey(): Result<Unit>

    suspend fun checkValidBasicKey(basicKey: String): BasicKeyAuthorizationResult

    suspend fun checkValidLocalSavedBasicKey(): BasicKeyAuthorizationResult
}
