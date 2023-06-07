package woowacourse.shopping.data.repository.impl

import woowacourse.shopping.data.datasource.MypageDataSource
import woowacourse.shopping.data.remote.result.DataResult
import woowacourse.shopping.data.repository.MypageRepository

class MypageRemoteRepository(
    private val mypageDataSource: MypageDataSource,
) : MypageRepository {
    override fun getCash(callback: (DataResult<Int>) -> Unit) {
        mypageDataSource.getCash(callback)
    }

    override fun chargeCash(cash: Int, callback: (DataResult<Int>) -> Unit) {
        mypageDataSource.chargeCash(cash, callback)
    }
}
