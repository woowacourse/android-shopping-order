package woowacourse.shopping.feature.cart.adapter

import androidx.recyclerview.widget.DiffUtil

class CartDiffUtil : DiffUtil.ItemCallback<CartGoodsItem>() {
    override fun areItemsTheSame(
        oldItem: CartGoodsItem,
        newItem: CartGoodsItem,
    ): Boolean = oldItem.cart.id == newItem.cart.id

    override fun areContentsTheSame(
        oldItem: CartGoodsItem,
        newItem: CartGoodsItem,
    ): Boolean = oldItem == newItem
}
