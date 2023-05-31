package woowacourse.shopping.data.remote.mapper

import woowacourse.shopping.data.model.DataEarnRate
import woowacourse.shopping.data.model.DataPoint
import woowacourse.shopping.data.model.DataUserPointInfo
import woowacourse.shopping.data.remote.response.UserPointInfoResponse

fun UserPointInfoResponse.toDataModel(): DataUserPointInfo =
    DataUserPointInfo(
        point = DataPoint(point),
        earnRate = DataEarnRate(earnRate)
    )
