package woowacourse.shopping.data.cash

interface CashDataSource {
    fun getCash(callback: (Int) -> Unit)

    fun chargeCash(cash: Int, callback: (Int) -> Unit)
}
