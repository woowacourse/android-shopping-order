package woowacourse.shopping.data.remote.response

import kotlinx.serialization.Serializable

@Serializable
data class OrderResponseDto(
    val id: Int,
    val name: String,
)
