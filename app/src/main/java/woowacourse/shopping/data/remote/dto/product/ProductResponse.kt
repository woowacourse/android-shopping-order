package woowacourse.shopping.data.remote.dto.product


import com.google.gson.annotations.SerializedName
import woowacourse.shopping.data.remote.dto.PageableDto
import woowacourse.shopping.data.remote.dto.SortDto

data class ProductResponse(
    @SerializedName("content")
    val productDto: List<ProductDto>,
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
    val sort: SortDto,
    @SerializedName("totalElements")
    val totalElements: Int,
    @SerializedName("totalPages")
    val totalPages: Int
)
