package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.UserPointInfo

interface PointRepository {
    fun getUserPointInfo(onReceived: (UserPointInfo) -> Unit)
}
