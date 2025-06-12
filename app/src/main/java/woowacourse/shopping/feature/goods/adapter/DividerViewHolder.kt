package woowacourse.shopping.feature.goods.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class DividerViewHolder(
    val binding: ItemDividerBinding,
) : RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun of(parent: ViewGroup): DividerViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemDividerBinding.inflate(inflater, parent, false)

            return DividerViewHolder(binding)
        }
    }
}
