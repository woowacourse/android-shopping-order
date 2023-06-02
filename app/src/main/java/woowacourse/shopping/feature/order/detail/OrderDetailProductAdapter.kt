package woowacourse.shopping.feature.order.detail

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.model.OrderProductUiModel

class OrderDetailProductAdapter :
    ListAdapter<OrderProductUiModel, OrderDetailProductViewHolder>(orderDetailProductDiffUtil) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderDetailProductViewHolder {
        return OrderDetailProductViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: OrderDetailProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun setItems(list: List<OrderProductUiModel>) {
        submitList(list)
    }

    companion object {
        private val orderDetailProductDiffUtil =
            object : DiffUtil.ItemCallback<OrderProductUiModel>() {
                override fun areItemsTheSame(
                    oldItem: OrderProductUiModel,
                    newItem: OrderProductUiModel
                ): Boolean {
                    return oldItem.productUiModel.id == newItem.productUiModel.id
                }

                override fun areContentsTheSame(
                    oldItem: OrderProductUiModel,
                    newItem: OrderProductUiModel
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
