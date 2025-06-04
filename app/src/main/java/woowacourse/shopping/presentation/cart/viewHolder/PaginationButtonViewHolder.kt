package woowacourse.shopping.presentation.cart.viewHolder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.databinding.PaginationButtonItemBinding
import woowacourse.shopping.presentation.cart.event.CartEventHandler

class PaginationButtonViewHolder(
    parent: ViewGroup,
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.pagination_button_item, parent, false)
) {
    private val binding: PaginationButtonItemBinding = PaginationButtonItemBinding.bind(itemView)

    fun bind(handler: CartEventHandler) {
        binding.handler = handler
    }
}
