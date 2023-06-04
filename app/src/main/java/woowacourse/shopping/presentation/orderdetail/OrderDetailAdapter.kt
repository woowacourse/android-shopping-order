package woowacourse.shopping.presentation.orderdetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.presentation.model.OrderProductModel

class OrderDetailAdapter(
    private val orderProductModels: List<OrderProductModel>,
) : RecyclerView.Adapter<OrderProductItemViewHolder>() {

    private lateinit var inflater: LayoutInflater

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderProductItemViewHolder {
        initLayoutInflater(parent)
        return OrderProductItemViewHolder(parent, inflater)
    }

    private fun initLayoutInflater(parent: ViewGroup) {
        if (!::inflater.isInitialized) {
            inflater = LayoutInflater.from(parent.context)
        }
    }

    override fun getItemCount() = orderProductModels.size

    override fun onBindViewHolder(holder: OrderProductItemViewHolder, position: Int) {
        holder.bind(orderProductModels[position])
    }
}
