package woowacourse.shopping.data.remote.dto.coupon


import com.google.gson.annotations.SerializedName

data class AvailableTimeDto(
    @SerializedName("end")
    val end: String,
    @SerializedName("start")
    val start: String
)
