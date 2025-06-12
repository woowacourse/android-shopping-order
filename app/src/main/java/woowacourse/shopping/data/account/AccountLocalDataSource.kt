package woowacourse.shopping.data.account

interface AccountLocalDataSource {
    suspend fun saveBasicKey(basicKey: String): Result<Unit>

    suspend fun loadBasicKey(): Result<String>
}
