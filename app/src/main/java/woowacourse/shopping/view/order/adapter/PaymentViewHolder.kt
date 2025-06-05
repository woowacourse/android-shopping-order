package woowacourse.shopping.view.order.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import woowacourse.shopping.databinding.ItemOrderPaymentBinding
import woowacourse.shopping.view.core.base.BaseViewHolder

class PaymentViewHolder(
    parent: ViewGroup,
) : BaseViewHolder<ItemOrderPaymentBinding>(
        ItemOrderPaymentBinding.inflate(LayoutInflater.from(parent.context), parent, false),
    ) {
    fun bind(item: OrderRvItems.PaymentItem) {
        binding.model = item.value
    }
}
