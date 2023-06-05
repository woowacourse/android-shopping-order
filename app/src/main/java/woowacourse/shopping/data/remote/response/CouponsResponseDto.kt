package woowacourse.shopping.data.remote.response

import kotlinx.serialization.Serializable

@Serializable
data class CouponsResponseDto(
    val id: Int,
    val name: String,
)
