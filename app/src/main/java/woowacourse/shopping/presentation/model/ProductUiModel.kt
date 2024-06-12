package woowacourse.shopping.presentation.model

import woowacourse.shopping.domain.model.Product
import java.io.Serializable

data class ProductUiModel(
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

fun ProductUiModel.toDomain(): Product {
    return Product(
        id = this.id,
        name = this.name,
        price = this.price,
        imageUrl = this.imageUrl,
        category = this.category,
    )
}
