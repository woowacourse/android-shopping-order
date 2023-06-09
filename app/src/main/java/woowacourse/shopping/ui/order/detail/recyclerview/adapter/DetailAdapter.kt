package woowacourse.shopping.ui.order.detail.recyclerview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemOrderDetailBinding
import woowacourse.shopping.model.UiOrderedProduct

class DetailAdapter(
    private val orderedProducts: List<UiOrderedProduct>,
) : RecyclerView.Adapter<DetailViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        val binding = ItemOrderDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        val item = orderedProducts[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return orderedProducts.size
    }
}
