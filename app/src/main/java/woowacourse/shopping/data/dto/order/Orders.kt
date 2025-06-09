package woowacourse.shopping.data.dto.order

import com.google.gson.annotations.SerializedName

data class Orders(
    @SerializedName("cartItemIds")
    val cartItemIds: List<Long>,
)
