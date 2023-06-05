package woowacourse.shopping.data.cash

class CashRepositoryDefault(private val cashDataSource: CashDataSource) : CashRepository {
    override fun chargeCash(cash: Int, callback: (Int) -> Unit) {
        cashDataSource.chargeCash(cash, callback)
    }

    override fun loadCash(callback: (Result<Int>) -> Unit) {
        cashDataSource.loadCash { result ->
            val x = result.onSuccess {
                Result.success(it)
            }.onFailure {
                Result.failure<Throwable>(it)
            }
            callback(x)
        }
    }
}
