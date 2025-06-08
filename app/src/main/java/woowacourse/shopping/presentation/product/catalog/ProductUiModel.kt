package woowacourse.shopping.presentation.product.catalog

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import woowacourse.shopping.domain.model.Product

@Parcelize
data class ProductUiModel(
    val id: Long,
    val imageUrl: String,
    val name: String,
    val price: Int,
    val quantity: Int = 0,
    val category: String = "",
    val isExpanded: Boolean = false,
    val isChecked: Boolean = false,
) : Parcelable

fun Product.toUiModel() =
    ProductUiModel(
        id = this.id,
        imageUrl = this.imageUrl,
        name = this.name,
        price = this.price,
        category = this.category,
        quantity = 0,
    )

fun ProductUiModel.toDomain() =
    Product(
        id = this.id,
        name = this.name,
        price = this.price,
        imageUrl = this.imageUrl,
        category = this.category,
    )
