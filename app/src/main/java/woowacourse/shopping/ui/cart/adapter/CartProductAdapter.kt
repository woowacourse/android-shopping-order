package woowacourse.shopping.ui.cart.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.databinding.ItemCartBinding
import woowacourse.shopping.domain.model.Product

class CartProductAdapter(
    private val onClickHandler: CartProductViewHolder.OnClickHandler,
) : ListAdapter<Product, CartProductViewHolder>(
        object : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(
                oldItem: Product,
                newItem: Product,
            ): Boolean = oldItem.cartId == newItem.cartId

            override fun areContentsTheSame(
                oldItem: Product,
                newItem: Product,
            ): Boolean = oldItem == newItem
        },
    ) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CartProductViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartProductViewHolder(binding, onClickHandler)
    }

    override fun onBindViewHolder(
        holder: CartProductViewHolder,
        position: Int,
    ) {
        val item: Product = getItem(position)
        holder.bind(item)
    }
}
