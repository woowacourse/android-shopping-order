package woowacourse.shopping.data.dto.order


import com.google.gson.annotations.SerializedName

data class AvailableTime(
    @SerializedName("end")
    val end: String,
    @SerializedName("start")
    val start: String
)