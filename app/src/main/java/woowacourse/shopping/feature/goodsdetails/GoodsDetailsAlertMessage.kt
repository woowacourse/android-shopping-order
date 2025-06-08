package woowacourse.shopping.feature.goodsdetails

import androidx.annotation.StringRes

sealed class GoodsDetailsAlertMessage {
    data class ResourceId(
        @StringRes
        val resourceId: Int,
    ) : GoodsDetailsAlertMessage()

    data class ResourceIdWithQuantity(
        @StringRes
        val resourceId: Int,
        val quantity: Int,
    ) : GoodsDetailsAlertMessage()
}
