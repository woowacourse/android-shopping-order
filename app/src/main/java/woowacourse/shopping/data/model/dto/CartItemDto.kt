package woowacourse.shopping.data.model.dto

import com.google.gson.annotations.SerializedName

data class CartItemDto(
    @SerializedName("content") val content: List<ContentDto>,
    @SerializedName("empty") val empty: Boolean,
    @SerializedName("first") val first: Boolean,
    @SerializedName("last") val last: Boolean,
    @SerializedName("number") val number: Int,
    @SerializedName("numberOfElements") val numberOfElements: Int,
    @SerializedName("pageable") val pageable: PageableDto,
    @SerializedName("size") val size: Int,
    @SerializedName("sort") val sort: SortDto,
    @SerializedName("totalElements") val totalElements: Int,
    @SerializedName("totalPages") val totalPages: Int,
)
