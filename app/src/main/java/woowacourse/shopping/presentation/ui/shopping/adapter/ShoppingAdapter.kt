package woowacourse.shopping.presentation.ui.shopping.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemProductBinding
import woowacourse.shopping.domain.model.ShoppingProduct
import woowacourse.shopping.presentation.ui.shopping.ShoppingEventHandler
import woowacourse.shopping.presentation.ui.shopping.ShoppingItemCountHandler
import woowacourse.shopping.presentation.ui.shopping.adapter.viewholder.ShoppingViewHolder

class ShoppingAdapter(
    private val shoppingEventHandler: ShoppingEventHandler,
    private val shoppingItemCountHandler: ShoppingItemCountHandler,
) : RecyclerView.Adapter<ShoppingViewHolder>() {
    private var shoppingProducts: List<ShoppingProduct> = emptyList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ShoppingViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShoppingViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ShoppingViewHolder,
        position: Int,
    ) {
        val shoppingProduct = shoppingProducts[position]
        return holder.bind(shoppingProduct, shoppingEventHandler, shoppingItemCountHandler)
    }

    override fun onBindViewHolder(
        holder: ShoppingViewHolder,
        position: Int,
        payloads: MutableList<Any>,
    ) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            payloads.forEach { payload ->
                when (payload) {
                    ShoppingAdapterPayload.QUANTITY_CHANGED -> {
                        holder.onQuantityChanged(shoppingProducts[position])
                    }

                    else -> {}
                }
            }
        }
    }


    override fun getItemCount(): Int {
        return shoppingProducts.size
    }

    fun setShoppingProducts(newList: List<ShoppingProduct>) {
        val positionStart = shoppingProducts.size
        shoppingProducts = newList
        notifyItemRangeInserted(positionStart, newList.size)
    }

    fun updateShoppingProducts(updatedIds: Set<Long>) {
        updatedIds.forEach { productId ->
            val updatedPosition = shoppingProducts.indexOfFirst { it.product.id == productId }
            notifyItemChanged(updatedPosition, ShoppingAdapterPayload.QUANTITY_CHANGED)
        }
    }
}
