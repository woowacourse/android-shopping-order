package woowacourse.shopping.view.util.product

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemProductBinding
import woowacourse.shopping.domain.model.Product

class ProductViewHolder(
    private val binding: ItemProductBinding,
    private val eventHandler: EventHandler,
) : RecyclerView.ViewHolder(binding.root) {
    private lateinit var currentItem: Product

    init {
        binding.handler = eventHandler
        binding.itemQuantityControl.tvIncrease.setOnClickListener {
            eventHandler.onQuantityIncreaseClick(currentItem)
        }
        binding.itemQuantityControl.tvDecrease.setOnClickListener {
            eventHandler.onQuantityDecreaseClick(currentItem)
        }
    }

    fun bind(
        product: Product,
        quantity: Int,
    ) {
        currentItem = product
        binding.product = product
        binding.quantity = quantity
    }

    interface EventHandler : QuantityControlEventHandler<Product> {
        fun onProductClick(item: Product)

        fun onAddClick(item: Product)
    }

    companion object {
        fun from(
            parent: ViewGroup,
            eventHandler: EventHandler,
        ): ProductViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemProductBinding.inflate(inflater, parent, false)
            return ProductViewHolder(binding, eventHandler)
        }
    }
}
