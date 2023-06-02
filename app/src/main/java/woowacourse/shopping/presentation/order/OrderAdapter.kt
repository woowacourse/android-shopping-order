package woowacourse.shopping.presentation.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.presentation.model.CartProductModel

class OrderAdapter(
    private val orderCartProducts: List<CartProductModel>,
) : RecyclerView.Adapter<OrderItemViewHolder>() {

    private lateinit var inflater: LayoutInflater

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        initLayoutInflater(parent)
        return OrderItemViewHolder(parent, inflater)
    }

    private fun initLayoutInflater(parent: ViewGroup) {
        if (!::inflater.isInitialized) {
            inflater = LayoutInflater.from(parent.context)
        }
    }

    override fun getItemCount() = orderCartProducts.size

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        holder.bind(orderCartProducts[position])
    }
}
