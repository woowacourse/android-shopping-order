package woowacourse.shopping.presentation.order

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.databinding.ItemOrderBinding
import woowacourse.shopping.presentation.model.OrderProductModel

class OrderItemsAdapter(
    private val updateProductPrice: (TextView, OrderProductModel) -> Unit,
) : ListAdapter<OrderProductModel, OrderItemViewHolder>(diffCallBack()) {

    private lateinit var itemOrderBinding: ItemOrderBinding
    private lateinit var inflater: LayoutInflater

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        if (!::inflater.isInitialized) {
            inflater = LayoutInflater.from(parent.context)
        }

        itemOrderBinding = ItemOrderBinding.inflate(
            inflater,
            parent,
            false,
        )
        return OrderItemViewHolder(itemOrderBinding, updateProductPrice)
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        holder.bind(orderProductModel = getItem(position))
    }

    companion object {
        fun diffCallBack() = object : DiffUtil.ItemCallback<OrderProductModel>() {
            override fun areItemsTheSame(
                oldItem: OrderProductModel,
                newItem: OrderProductModel
            ): Boolean = oldItem.product == newItem.product

            override fun areContentsTheSame(
                oldItem: OrderProductModel,
                newItem: OrderProductModel
            ): Boolean = oldItem == newItem
        }
    }
}
