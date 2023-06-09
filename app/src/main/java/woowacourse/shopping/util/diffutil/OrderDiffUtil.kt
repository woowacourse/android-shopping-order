package woowacourse.shopping.util.diffutil

import androidx.recyclerview.widget.DiffUtil
import woowacourse.shopping.model.UiOrderResponse

object OrderDiffUtil : DiffUtil.ItemCallback<UiOrderResponse>() {
    override fun areItemsTheSame(oldItem: UiOrderResponse, newItem: UiOrderResponse): Boolean {
        return oldItem.orderId == newItem.orderId
    }

    override fun areContentsTheSame(oldItem: UiOrderResponse, newItem: UiOrderResponse): Boolean {
        return oldItem == newItem
    }
}
