package woowacourse.shopping.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class PointResponse(val availablePoint: Int = 0)
