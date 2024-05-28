package woowacourse.shopping.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class ResponseCartItemsCountGetDto(
    val quantity:Int
)
