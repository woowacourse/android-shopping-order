package woowacourse.shopping.domain.product

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import woowacourse.shopping.domain.Quantity

@Parcelize
data class Product(
    val id: Long,
    val name: String,
    val imgUrl: String,
    val category: String,
    private val price: Price,
    val quantity: Quantity = Quantity(0),
) : Parcelable {
    val priceValue: Int = price.value
}
