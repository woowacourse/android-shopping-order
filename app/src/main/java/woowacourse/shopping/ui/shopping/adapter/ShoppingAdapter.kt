package woowacourse.shopping.ui.shopping.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.ui.shopping.ShoppingEvent
import woowacourse.shopping.ui.shopping.uistate.ProductUIState

class ShoppingAdapter(
    private val shoppingEvent: ShoppingEvent
) : ListAdapter<ProductUIState, ShoppingViewHolder>(ShoppingDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingViewHolder {
        return ShoppingViewHolder.from(
            parent, shoppingEvent
        )
    }

    override fun onBindViewHolder(holder: ShoppingViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun addItems(newProducts: List<ProductUIState>) {
        val products = currentList.toMutableList()
        products.addAll(newProducts)
        submitList(products)
    }

    fun changeItem(newProduct: ProductUIState) {
        val products = currentList.toMutableList()
        val index = products.indexOfFirst { newProduct.id == it.id }
        if (index == -1) return
        products[index] = newProduct
        submitList(products)
    }

    fun setItems(newProduct: List<ProductUIState>) {
        submitList(newProduct)
    }
}
