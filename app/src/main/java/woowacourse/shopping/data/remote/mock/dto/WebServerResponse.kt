package woowacourse.shopping.data.remote.mock.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class WebServerResponse(
    @SerialName("productId")
    val id: Long,
    val name: String,
    val price: Int,
    @SerialName("imageUrl")
    val imageUri: String,
    val category: String,
)
