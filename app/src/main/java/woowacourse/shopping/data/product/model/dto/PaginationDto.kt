package woowacourse.shopping.data.product.model.dto

import com.google.gson.annotations.SerializedName

data class PaginationDto(
    @SerializedName("total") val total: Int,
    @SerializedName("perPage") val perPage: Int,
    @SerializedName("currentPage") val currentPage: Int,
    @SerializedName("lastPage") val lastPage: Int
)
