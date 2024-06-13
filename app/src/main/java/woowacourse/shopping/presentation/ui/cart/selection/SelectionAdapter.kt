package woowacourse.shopping.presentation.ui.cart.selection

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.databinding.ItemCartBinding
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.presentation.ui.counter.CounterHandler

class SelectionAdapter(
    private val selectionEventHandler: SelectionEventHandler,
    private val selectionCountHandler: CounterHandler,
) : ListAdapter<CartItem, SelectionViewHolder>(diffCallback) {
    // private var cartItems: List<CartItem> = emptyList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SelectionViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SelectionViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: SelectionViewHolder,
        position: Int,
    ) {
        return holder.bind(getItem(position), selectionEventHandler, selectionCountHandler)
    }

    companion object {
        val diffCallback =
            object : DiffUtil.ItemCallback<CartItem>() {
                override fun areItemsTheSame(
                    oldItem: CartItem,
                    newItem: CartItem,
                ): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(
                    oldItem: CartItem,
                    newItem: CartItem,
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
