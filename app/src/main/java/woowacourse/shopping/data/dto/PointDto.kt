package woowacourse.shopping.data.dto

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class PointResponse(
    @SerializedName("availablePoint")
    val availablePoint: Int = 0,
)
