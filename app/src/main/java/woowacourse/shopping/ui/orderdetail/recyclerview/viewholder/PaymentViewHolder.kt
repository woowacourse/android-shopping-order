package woowacourse.shopping.ui.orderdetail.recyclerview.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemPaymentBinding
import woowacourse.shopping.model.PaymentModel
import woowacourse.shopping.ui.order.recyclerview.ListItem
import woowacourse.shopping.ui.order.recyclerview.viewholder.BaseViewHolder

class PaymentViewHolder(parent: ViewGroup) : BaseViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_payment, parent, false)
) {
    private val binding: ItemPaymentBinding = ItemPaymentBinding.bind(itemView)

    override fun bind(item: ListItem) {
        binding.payment = item as? PaymentModel
    }
}
