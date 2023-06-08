package woowacourse.shopping.data.httpclient.mapper

import woowacourse.shopping.data.httpclient.response.UserPointInfoResponse
import woowacourse.shopping.data.model.DataEarnRate
import woowacourse.shopping.data.model.DataPoint
import woowacourse.shopping.data.model.DataUserPointInfo

fun UserPointInfoResponse.toDataModel(): DataUserPointInfo =
    DataUserPointInfo(
        point = DataPoint(point),
        earnRate = DataEarnRate(earnRate)
    )
