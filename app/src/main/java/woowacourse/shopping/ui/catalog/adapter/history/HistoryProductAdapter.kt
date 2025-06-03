package woowacourse.shopping.ui.catalog.adapter.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.databinding.ItemHistoryProductBinding
import woowacourse.shopping.domain.model.HistoryProduct

class HistoryProductAdapter(
    private val onClickHandler: HistoryProductViewHolder.OnClickHandler,
) : ListAdapter<HistoryProduct, HistoryProductViewHolder>(
        object : DiffUtil.ItemCallback<HistoryProduct>() {
            override fun areItemsTheSame(
                oldItem: HistoryProduct,
                newItem: HistoryProduct,
            ): Boolean = oldItem.productId == newItem.productId

            override fun areContentsTheSame(
                oldItem: HistoryProduct,
                newItem: HistoryProduct,
            ): Boolean = oldItem == newItem
        },
    ) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): HistoryProductViewHolder {
        val binding =
            ItemHistoryProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryProductViewHolder(binding, onClickHandler)
    }

    override fun onBindViewHolder(
        holder: HistoryProductViewHolder,
        position: Int,
    ) {
        val item: HistoryProduct = getItem(position)
        holder.bind(item)
    }
}
