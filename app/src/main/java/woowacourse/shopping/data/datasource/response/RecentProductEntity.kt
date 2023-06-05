package woowacourse.shopping.data.datasource.response

import com.google.gson.annotations.SerializedName

data class RecentProductEntity(
    @SerializedName("id")
    val id: Int,
    @SerializedName("product")
    val product: ProductEntity,
)
