package woowacourse.shopping.domain.product

import woowacourse.shopping.domain.Quantity
import java.io.Serializable

data class Product(
    val id: Long,
    val name: String,
    val imgUrl: String,
    val category: String,
    private val price: Price,
    val quantity: Quantity = Quantity(0),
) : Serializable {
    val priceValue: Int = price.value
}
