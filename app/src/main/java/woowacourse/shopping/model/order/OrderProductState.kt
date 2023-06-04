package woowacourse.shopping.model.order

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import woowacourse.shopping.model.ProductState

@Parcelize
class OrderProductState(
    val id: Long,
    val quantity: Int,
    val product: ProductState
) : Parcelable
