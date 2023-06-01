package woowacourse.shopping.model

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import woowacourse.shopping.ui.order.recyclerview.ListItem
import woowacourse.shopping.ui.order.recyclerview.OrderViewType

@Parcelize
data class OrderProductModel(
    val cartProductId: Int,
    val name: String,
    val price: UiPrice,
    val totalPrice: UiPrice,
    val quantity: UiProductCount,
    val imageUrl: String,
) : Parcelable, ListItem {
    @IgnoredOnParcel
    override val viewType: Int = OrderViewType.ORDER.value

    companion object {
        const val VIEW_TYPE_VALUE = 1
    }
}
