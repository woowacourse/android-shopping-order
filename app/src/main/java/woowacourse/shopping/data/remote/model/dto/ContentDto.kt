package woowacourse.shopping.data.remote.model.dto

import com.google.gson.annotations.SerializedName

data class ContentDto(
    @SerializedName("id") val id: Long,
    @SerializedName("product") val product: ProductDto,
    @SerializedName("quantity") val quantity: Int,
)
