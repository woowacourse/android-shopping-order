package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.UserPointData

interface PointRepository {
    fun getUserPointData(onReceived: (UserPointData) -> Unit)
}
