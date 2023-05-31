package woowacourse.shopping.data.dataSource

import woowacourse.shopping.domain.util.WoowaResult

interface CashDataSource {
    fun charge(callback: (WoowaResult<Int>) -> Unit, amount: Int)
    fun fetchCashAmount(callback: (WoowaResult<Int>) -> Unit)
}
