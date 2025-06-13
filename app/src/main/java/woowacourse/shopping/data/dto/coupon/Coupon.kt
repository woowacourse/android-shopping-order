package woowacourse.shopping.data.dto.coupon

import com.google.gson.annotations.SerializedName
import java.time.LocalDate

data class Coupon(
    @SerializedName("id")
    val id: Long,
    @SerializedName("code")
    val code: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("expirationDate")
    val expirationDate: String,
    @SerializedName("discountType")
    val discountType: String,
    @SerializedName("discount")
    val discount: Int,
    @SerializedName("minimumAmount")
    val minimumAmount: Int,
)
