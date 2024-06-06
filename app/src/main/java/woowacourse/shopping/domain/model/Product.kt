package woowacourse.shopping.domain.model

import java.io.Serializable

data class Product(
    val id: Long,
    val name: String,
    val price: Int,
    val category: String,
    val imageUrl: String,
    val quantity: Int = INIT_QUANTITY,
) : Serializable {
    companion object {
        const val INIT_QUANTITY = 0
    }
}
