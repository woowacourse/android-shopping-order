package woowacourse.shopping.presentation.orderlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.presentation.model.OrderModel

class OrderListAdapter(
    private val orderModels: List<OrderModel>,
    private val showOrderDetail: (OrderModel) -> Unit,
) : RecyclerView.Adapter<OrderItemViewHolder>() {

    private lateinit var inflater: LayoutInflater

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        initLayoutInflater(parent)
        return OrderItemViewHolder(parent, inflater, showOrderDetail)
    }

    private fun initLayoutInflater(parent: ViewGroup) {
        if (!::inflater.isInitialized) {
            inflater = LayoutInflater.from(parent.context)
        }
    }

    override fun getItemCount() = orderModels.size

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        holder.bind(orderModels[position])
    }
}
