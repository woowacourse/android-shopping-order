package woowacourse.shopping.feature.goods.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemLoadMoreBinding

class LoadMoreViewHolder(
    private val binding: ItemLoadMoreBinding,
    private val goodsClickListener: GoodsClickListener,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind() {
        binding.goodsClickListener = goodsClickListener
    }

    companion object {
        fun create(
            parent: ViewGroup,
            goodsClickListener: GoodsClickListener,
        ): LoadMoreViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemLoadMoreBinding.inflate(inflater, parent, false)
            return LoadMoreViewHolder(binding, goodsClickListener)
        }
    }
}
