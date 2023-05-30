package woowacourse.shopping.ui.model

import java.io.Serializable

data class OrderProduct(
    val productId: Long,
    val productName: String,
    val quantity: String,
    val price: Long,
    val imageUrl: String,
) : Serializable
