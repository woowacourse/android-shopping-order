package woowacourse.shopping.data.account

interface AccountLocalDataSource {
    fun saveBasicKey(
        basicKey: String,
        onComplete: () -> Unit,
        onFail: () -> Unit,
    )

    fun loadBasicKey(
        onComplete: (basicKey: String) -> Unit,
        onFail: () -> Unit,
    )
}
