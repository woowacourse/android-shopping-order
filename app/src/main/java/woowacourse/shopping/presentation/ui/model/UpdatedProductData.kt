package woowacourse.shopping.presentation.ui.model
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UpdatedProductData(
    val productId: Long,
    val newQuantity: Int,
) : Parcelable
