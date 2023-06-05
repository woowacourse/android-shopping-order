package woowacourse.shopping.data.cash

interface CashDataSource {
    fun loadCash(callback: (Result<Int>) -> Unit)

    fun chargeCash(cash: Int, callback: (Int) -> Unit)
}
