package woowacourse.shopping.ui.cart.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.HolderCartBinding
import woowacourse.shopping.ui.OnItemQuantityChangeListener
import woowacourse.shopping.ui.OnProductItemClickListener
import woowacourse.shopping.ui.cart.listener.OnCartItemSelectedListener
import woowacourse.shopping.ui.model.CartItem

class CartItemAdapter(
    private val onProductItemClickListener: OnProductItemClickListener,
    private val onItemQuantityChangeListener: OnItemQuantityChangeListener,
    private val onCartItemSelectedListener: OnCartItemSelectedListener,
) : RecyclerView.Adapter<CartItemViewHolder>() {
    private var products: List<CartItem> = emptyList()

    private lateinit var recyclerView: RecyclerView

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
        recyclerView.itemAnimator = null
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CartItemViewHolder =
        CartItemViewHolder(
            HolderCartBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onProductItemClickListener,
            onItemQuantityChangeListener,
            onCartItemSelectedListener,
        )

    override fun onBindViewHolder(
        holder: CartItemViewHolder,
        position: Int,
    ) = holder.bind(products[position])

    override fun getItemCount(): Int = products.size

    fun updateData(newData: List<CartItem>) {
        val oldSize = products.size
        this.products = newData.toList()
        if (newData.isEmpty()) {
            notifyItemRangeRemoved(0, oldSize)
            return
        }

        notifyItemRangeChanged(0, itemCount)
    }
}
