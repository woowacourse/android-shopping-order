package woowacourse.shopping.presentation.cart.viewHolder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.PaginationButtonItemBinding
import woowacourse.shopping.presentation.cart.event.CartEventHandler

class PaginationButtonViewHolder(
    private val binding: PaginationButtonItemBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(handler: CartEventHandler) {
        binding.handler = handler
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): PaginationButtonViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = PaginationButtonItemBinding.inflate(inflater, parent, false)
            return PaginationButtonViewHolder(binding)
        }
    }
}
