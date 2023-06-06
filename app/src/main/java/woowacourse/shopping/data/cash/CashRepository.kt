package woowacourse.shopping.data.cash

interface CashRepository {
    fun loadCash(callback: (Result<Int>) -> Unit)

    fun chargeCash(cash: Int, callback: (Int) -> Unit)
}