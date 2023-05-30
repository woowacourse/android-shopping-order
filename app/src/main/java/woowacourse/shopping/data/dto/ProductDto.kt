package woowacourse.shopping.data.dto

data class ProductGetResponse(
    val id: Int,
    val imageUrl: String,
    val name: String,
    val price: Int
)

data class ProductPostRequest(
    val name: String,
    val price: Int,
    val imageUrl: String
)

data class ProductPutRequest(
    val name: String,
    val price: Int,
    val imageUrl: String
)

data class ProductDeleteRequest(
    val name: String,
    val price: Int,
    val imageUrl: String
)
