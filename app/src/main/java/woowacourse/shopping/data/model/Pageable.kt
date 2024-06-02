package woowacourse.shopping.data.model

import com.google.gson.annotations.SerializedName

data class Pageable(
    val offset: Int,
    val pageNumber: Int,
    val pageSize: Int,
    val paged: Boolean,
    val sort: Sort,
    val unpaged: Boolean,
)
