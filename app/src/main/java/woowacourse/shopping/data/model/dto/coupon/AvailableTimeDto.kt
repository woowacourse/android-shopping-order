package woowacourse.shopping.data.model.dto.coupon

import com.google.gson.annotations.SerializedName

data class AvailableTimeDto(
    @SerializedName("start")
    val start: String,

    @SerializedName("end")
    val end: String,
)
