package woowacourse.shopping.feature.payment

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemPaymentBinding
import woowacourse.shopping.model.CartProductUiModel

class PaymentItemViewHolder(
    private val binding: ItemPaymentBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: CartProductUiModel) {
        binding.cartProduct = item
    }
}
