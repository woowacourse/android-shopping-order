package woowacourse.shopping.backend.retrofit.dto

import kotlinx.serialization.Serializable

@Serializable
data class ShoppingCartPageable(
    val offset: Long,
    val pageNumber: Int,
    val pageSize: Int,
    val paged: Boolean,
    val sort: Sort,
    val unpaged: Boolean,
)
