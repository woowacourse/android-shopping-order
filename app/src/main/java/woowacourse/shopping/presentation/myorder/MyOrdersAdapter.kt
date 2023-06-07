package woowacourse.shopping.presentation.myorder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.databinding.ItemMyOrderBinding
import woowacourse.shopping.presentation.model.OrderModel

class MyOrdersAdapter(
    private val showOrderDetail: (OrderModel) -> Unit,
) : ListAdapter<OrderModel, MyOrderItemViewHolder>(diffCallBack()) {
    private lateinit var binding: ItemMyOrderBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyOrderItemViewHolder {
        binding = ItemMyOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyOrderItemViewHolder(binding, showOrderDetail)
    }

    override fun onBindViewHolder(holder: MyOrderItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        fun diffCallBack() = object : DiffUtil.ItemCallback<OrderModel>() {
            override fun areItemsTheSame(
                oldItem: OrderModel,
                newItem: OrderModel
            ): Boolean = oldItem.orderId == newItem.orderId

            override fun areContentsTheSame(
                oldItem: OrderModel,
                newItem: OrderModel
            ): Boolean = oldItem == newItem
        }
    }
}
