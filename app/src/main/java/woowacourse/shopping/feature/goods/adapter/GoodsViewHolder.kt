package woowacourse.shopping.feature.goods.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemGoodsBinding
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.feature.CustomCartQuantity

class GoodsViewHolder(
    private val binding: ItemGoodsBinding,
    private val goodsClickListener: GoodsClickListener,
) : RecyclerView.ViewHolder(binding.root) {
    private lateinit var currentCart: CartProduct

    init {
        binding.goodsClickListener = goodsClickListener
        binding.customCartQuantity.setClickListener(
            object : CustomCartQuantity.CartQuantityClickListener {
                override fun onAddClick() {
                    goodsClickListener.insertToCart(currentCart)
                }

                override fun onRemoveClick() {
                    goodsClickListener.removeFromCart(currentCart)
                }
            },
        )
    }

    fun bind(cart: CartProduct) {
        currentCart = cart
        binding.cart = cart
    }

    companion object {
        fun create(
            parent: ViewGroup,
            listener: GoodsClickListener,
        ): GoodsViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemGoodsBinding.inflate(inflater, parent, false)
            return GoodsViewHolder(binding, listener)
        }
    }
}
