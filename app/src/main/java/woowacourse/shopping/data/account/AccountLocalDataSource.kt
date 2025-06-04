package woowacourse.shopping.data.account

interface AccountLocalDataSource {
    fun saveBasicKey(
        basicKey: String,
        onComplete: () -> Unit,
        onFail: () -> Unit,
    )
}
