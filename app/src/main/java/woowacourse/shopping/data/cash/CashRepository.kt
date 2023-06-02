package woowacourse.shopping.data.cash

interface CashRepository {
    fun getCash(callback: (Int) -> Unit)

    fun chargeCash(cash: Int, callback: (Int) -> Unit)
}
