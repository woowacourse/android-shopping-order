package woowacourse.shopping.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductGetResponse(
    val id: Int,
    val imageUrl: String,
    val name: String,
    val price: Int,
)

@Serializable
data class ProductPostRequest(
    val name: String,
    val price: Int,
    val imageUrl: String,
)

@Serializable
data class ProductPutRequest(
    val name: String,
    val price: Int,
    val imageUrl: String,
)

@Serializable
data class ProductDeleteRequest(
    val name: String,
    val price: Int,
    val imageUrl: String,
)
