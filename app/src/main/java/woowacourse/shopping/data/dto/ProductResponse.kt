package woowacourse.shopping.data.dto

import com.google.gson.annotations.SerializedName

data class ProductResponse(
    @SerializedName("content")
    val products: List<ProductDto>,
    @SerializedName("empty")
    val empty: Boolean,
    @SerializedName("first")
    val first: Boolean,
    @SerializedName("last")
    val last: Boolean,
    @SerializedName("number")
    val number: Int,
    @SerializedName("numberOfElements")
    val numberOfElements: Int,
    @SerializedName("pageable")
    val pageableDto: PageableDto,
    @SerializedName("size")
    val size: Int,
    @SerializedName("sort")
    val sortDto: SortDto,
    @SerializedName("totalElements")
    val totalElements: Int,
    @SerializedName("totalPages")
    val totalPages: Int,
)
