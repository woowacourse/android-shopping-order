package woowacourse.shopping.data.remote.request

import com.google.gson.annotations.SerializedName

data class CouponDTO(
    @SerializedName("memberCouponId")
    val id: Long,
    val name: String,
)
