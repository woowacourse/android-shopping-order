package woowacourse.shopping.viewmodel.fakes.server.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class MockProductResponse(
    @SerialName("productId")
    val id: Long,
    val name: String,
    val price: Int,
    @SerialName("imageUrl")
    val imageUri: String,
    val category: String,
)
