package woowacourse.shopping.data.account

interface AccountLocalDataSource {
    fun saveBasicKey(basicKey: String): Result<Unit>

    fun loadBasicKey(): Result<String>
}
