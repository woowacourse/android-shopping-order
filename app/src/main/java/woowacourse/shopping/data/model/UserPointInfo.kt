package woowacourse.shopping.data.model

typealias DataUserPointInfo = UserPointData

data class UserPointData(
    val point: DataPoint,
    val earnRate: DataEarnRate
)
