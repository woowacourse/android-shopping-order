package woowacourse.shopping.presentation.common

import androidx.recyclerview.widget.DiffUtil
import woowacourse.shopping.presentation.model.OrderCartInfoModel

class OrderCartDiffItemCallback : DiffUtil.ItemCallback<OrderCartInfoModel>() {
    override fun areItemsTheSame(
        oldItem: OrderCartInfoModel,
        newItem: OrderCartInfoModel,
    ): Boolean = (oldItem.productModel.id == newItem.productModel.id)

    override fun areContentsTheSame(
        oldItem: OrderCartInfoModel,
        newItem: OrderCartInfoModel,
    ): Boolean = oldItem == newItem
}
