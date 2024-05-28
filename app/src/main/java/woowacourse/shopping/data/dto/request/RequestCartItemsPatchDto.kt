package woowacourse.shopping.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class RequestCartItemsPatchDto(
    val quantity:Int
)
