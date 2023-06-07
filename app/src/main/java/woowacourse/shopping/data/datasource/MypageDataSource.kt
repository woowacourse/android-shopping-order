package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.remote.result.DataResult

interface MypageDataSource {
    fun getCash(callback: (DataResult<Int>) -> Unit)
    fun chargeCash(cash: Int, callback: (DataResult<Int>) -> Unit)
}
