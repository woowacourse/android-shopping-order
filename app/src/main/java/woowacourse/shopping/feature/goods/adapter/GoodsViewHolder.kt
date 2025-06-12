package woowacourse.shopping.feature.goods.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemGoodsBinding
import woowacourse.shopping.feature.CustomCartQuantity

class GoodsViewHolder(
    private val binding: ItemGoodsBinding,
    private val goodsClickListener: GoodsClickListener,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(model: GoodsRvItems.GoodsItem) {
        val item = model.item

        binding.model = item
        binding.goodsClickListener = goodsClickListener
        binding.customCartQuantity.setClickListener(
            object : CustomCartQuantity.CartQuantityClickListener {
                override fun onAddClick() {
                    goodsClickListener.increaseQuantity(item)
                }

                override fun onRemoveClick() {
                    goodsClickListener.decreaseQuantity(item)
                }
            },
        )
    }

    companion object {
        fun of(
            parent: ViewGroup,
            goodsClickListener: GoodsClickListener,
        ): GoodsViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemGoodsBinding.inflate(inflater, parent, false)

            return GoodsViewHolder(binding, goodsClickListener)
        }
    }
}
