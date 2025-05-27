package woowacourse.shopping.feature.goods.adapter

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.data.remote.Product
import woowacourse.shopping.databinding.ItemGoodsBinding
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.feature.CustomCartQuantity

class GoodsViewHolder(
    private val binding: ItemGoodsBinding,
    private val goodsClickListener: GoodsClickListener,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(product: Product) {
        binding.product = product
        binding.goodsClickListener = goodsClickListener
//        binding.customCartQuantity.setClickListener(
//            object : CustomCartQuantity.CartQuantityClickListener {
//                override fun onAddClick() {
//                    goodsClickListener.insertToCart(cart)
//                }
//
//                override fun onRemoveClick() {
//                    goodsClickListener.removeFromCart(cart)
//                }
//            },
//        )
    }
}
