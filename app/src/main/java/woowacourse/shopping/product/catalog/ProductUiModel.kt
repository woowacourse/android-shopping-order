package woowacourse.shopping.product.catalog

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.junit.experimental.categories.Category

@Parcelize
data class ProductUiModel(
    val id: Int,
    val imageUrl: String,
    val name: String,
    val price: Int,
    val quantity: Int = 0,
    val cartItemId: Int? = null,
    val isChecked: Boolean = true,
    val category: String? = null
) : Parcelable
