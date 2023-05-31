package woowacourse.shopping.data.model

typealias DataUserPointData = UserPointData

data class UserPointData(
    val point: DataPoint,
    val earnRate: DataEarnRate
)
