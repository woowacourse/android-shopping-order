package woowacourse.shopping.view.cart.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.view.core.handler.CartQuantityHandler
import woowacourse.shopping.view.main.adapter.ProductRvItems
import woowacourse.shopping.view.main.adapter.ProductViewHolder

class RecommendAdapter(
    private val items: List<ProductRvItems.ProductItem>,
    private val handler: Handler,
) : RecyclerView.Adapter<ProductViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ProductViewHolder {
        return ProductViewHolder(
            parent,
            handler as ProductViewHolder.Handler,
            handler as CartQuantityHandler,
        )
    }

    override fun onBindViewHolder(
        holder: ProductViewHolder,
        position: Int,
    ) {
        holder.bind(items[position])
    }

    interface Handler : ProductViewHolder.Handler

    override fun getItemCount(): Int = items.size
}
