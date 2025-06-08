package woowacourse.shopping.view.payment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemPaymentInformationBinding

class PaymentInformationViewHolder(
    binding: ItemPaymentInformationBinding,
) : RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun from(parent: ViewGroup): PaymentInformationViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemPaymentInformationBinding.inflate(inflater, parent, false)
            return PaymentInformationViewHolder(binding)
        }
    }
}
