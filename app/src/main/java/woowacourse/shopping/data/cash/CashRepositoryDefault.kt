package woowacourse.shopping.data.cash

class CashRepositoryDefault(private val cashDataSource: CashDataSource) : CashRepository {
    override fun chargeCash(cash: Int, callback: (Int) -> Unit) {
        cashDataSource.chargeCash(cash, callback)
    }

    override fun loadCash(callback: (Int) -> Unit) {
        cashDataSource.loadCash(callback)
    }
}
