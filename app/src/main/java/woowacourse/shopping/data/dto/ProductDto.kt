package woowacourse.shopping.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductGetResponse(
    @SerialName("id")
    val id: Int,
    @SerialName("imageUrl")
    val imageUrl: String,
    @SerialName("name")
    val name: String,
    @SerialName("price")
    val price: Int,
)

@Serializable
data class ProductPostRequest(
    @SerialName("name")
    val name: String,
    @SerialName("price")
    val price: Int,
    @SerialName("imageUrl")
    val imageUrl: String,
)

@Serializable
data class ProductPutRequest(
    @SerialName("name")
    val name: String,
    @SerialName("price")
    val price: Int,
    @SerialName("imageUrl")
    val imageUrl: String,
)

@Serializable
data class ProductDeleteRequest(
    @SerialName("name")
    val name: String,
    @SerialName("price")
    val price: Int,
    @SerialName("imageUrl")
    val imageUrl: String,
)
