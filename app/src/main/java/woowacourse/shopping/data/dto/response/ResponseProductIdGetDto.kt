package woowacourse.shopping.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class ResponseProductIdGetDto(
    val category: String,
    val id: Int,
    val imageUrl: String,
    val name: String,
    val price: Int,
)
