package woowacourse.shopping.ui.productlist.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.ui.productlist.ProductListEvent
import woowacourse.shopping.ui.productlist.uistate.ProductUIState

class ProductListAdapter(
    private val products: MutableList<ProductUIState> = mutableListOf(),
    private val productListEvent: ProductListEvent
) : RecyclerView.Adapter<ProductListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductListViewHolder {
        return ProductListViewHolder.from(
            parent, productListEvent
        )
    }

    override fun getItemCount(): Int = products.size

    override fun onBindViewHolder(holder: ProductListViewHolder, position: Int) {
        holder.bind(products[position])
    }

    fun addItems(newProducts: List<ProductUIState>) {
        products.addAll(newProducts)
        notifyDataSetChanged()
    }

    fun changeItem(newProduct: ProductUIState) {
        val index = products.indexOfFirst { newProduct.id == it.id }
        if (index == -1) return
        products[index] = newProduct
        notifyItemChanged(index)
    }

    fun setItems(newProduct: List<ProductUIState>) {
        products.clear()
        products.addAll(newProduct)
        notifyDataSetChanged()
    }
}
