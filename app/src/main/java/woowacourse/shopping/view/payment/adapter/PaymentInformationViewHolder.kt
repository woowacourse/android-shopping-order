package woowacourse.shopping.view.payment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemPaymentInformationBinding
import woowacourse.shopping.domain.model.Order

class PaymentInformationViewHolder(
    private val binding: ItemPaymentInformationBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(order: Order) {
        binding.order = order
    }

    companion object {
        fun from(parent: ViewGroup): PaymentInformationViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemPaymentInformationBinding.inflate(inflater, parent, false)
            return PaymentInformationViewHolder(binding)
        }
    }
}
