package woowacourse.shopping.data.repository

import woowacourse.shopping.data.remote.result.DataResult

interface MypageRepository {
    fun getCash(callback: (DataResult<Int>) -> Unit)
    fun chargeCash(cash: Int, callback: (DataResult<Int>) -> Unit)
}
