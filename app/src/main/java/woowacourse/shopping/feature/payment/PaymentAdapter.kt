package woowacourse.shopping.feature.payment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemPaymentBinding
import woowacourse.shopping.model.CartProductUiModel

class PaymentAdapter(
    private val items: List<CartProductUiModel>
) : RecyclerView.Adapter<PaymentItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentItemViewHolder {
        val binding = DataBindingUtil.inflate<ItemPaymentBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_payment,
            parent,
            false
        )
        return PaymentItemViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: PaymentItemViewHolder, position: Int) {
        holder.bind(items[position])
    }
}
