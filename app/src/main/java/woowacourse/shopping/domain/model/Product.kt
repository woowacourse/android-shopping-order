package woowacourse.shopping.domain.model

import java.io.Serializable

data class Product(
    val id: Int,
    val name: String,
    val price: Int,
    val imageUrl: String,
    val category: String?,
) : Serializable
