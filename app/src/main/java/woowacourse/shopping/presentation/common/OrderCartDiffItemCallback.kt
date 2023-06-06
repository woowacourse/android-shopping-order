package woowacourse.shopping.presentation.common

import androidx.recyclerview.widget.DiffUtil
import woowacourse.shopping.presentation.model.OrderCartModel

class OrderCartDiffItemCallback : DiffUtil.ItemCallback<OrderCartModel>() {
    override fun areItemsTheSame(
        oldItem: OrderCartModel,
        newItem: OrderCartModel,
    ): Boolean = (oldItem.productModel.id == newItem.productModel.id)

    override fun areContentsTheSame(
        oldItem: OrderCartModel,
        newItem: OrderCartModel,
    ): Boolean = oldItem == newItem
}
