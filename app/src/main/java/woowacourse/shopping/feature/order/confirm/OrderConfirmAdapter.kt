package woowacourse.shopping.feature.order.confirm

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.model.OrderProductUiModel

class OrderConfirmAdapter :
    ListAdapter<OrderProductUiModel, OrderConfirmViewHolder>(OrderConfirmDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderConfirmViewHolder {
        return OrderConfirmViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: OrderConfirmViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    fun setOrderProducts(list: List<OrderProductUiModel>) {
        submitList(list)
    }

    companion object {
        private val OrderConfirmDiffUtil = object : DiffUtil.ItemCallback<OrderProductUiModel>() {
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
