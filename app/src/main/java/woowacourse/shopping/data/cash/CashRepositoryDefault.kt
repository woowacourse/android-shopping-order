package woowacourse.shopping.data.cash

class CashRepositoryDefault(val cashDataSource: CashDataSource) : CashRepository {
    override fun chargeCash(cash: Int, callback: (Int) -> Unit) {
        cashDataSource.chargeCash(cash, callback)
    }

    override fun getCash(callback: (Int) -> Unit) {
        cashDataSource.getCash(callback)
    }
}
