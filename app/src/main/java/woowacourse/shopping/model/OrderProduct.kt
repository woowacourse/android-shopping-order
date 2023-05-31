package woowacourse.shopping.model

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import woowacourse.shopping.ui.order.recyclerview.ListItem
import woowacourse.shopping.ui.order.recyclerview.OrderViewType

typealias UiOrderProduct = OrderProduct

@Parcelize
data class OrderProduct(
    val productId: Int,
    val name: String,
    val price: UiPrice,
    val totalPrice: UiPrice,
    val quantity: UiProductCount,
    val imageUrl: String,
) : Parcelable, ListItem {
    @IgnoredOnParcel
    override val viewType: Int = OrderViewType.ORDER.value
}
