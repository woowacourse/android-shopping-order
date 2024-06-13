package woowacourse.shopping.data.remote.model.dto

import com.google.gson.annotations.SerializedName

data class ProductResponseDto(
    @SerializedName("content") val content: List<ProductDto>,
    @SerializedName("pageable") val pageable: PageableDto,
    @SerializedName("last") val last: Boolean,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("totalElements") val totalElements: Int,
    @SerializedName("sort") val sort: SortDto,
    @SerializedName("first") val first: Boolean,
    @SerializedName("number") val number: Int,
    @SerializedName("numberOfElements") val numberOfElements: Int,
    @SerializedName("size") val size: Int,
    @SerializedName("empty") val empty: Boolean,
)
