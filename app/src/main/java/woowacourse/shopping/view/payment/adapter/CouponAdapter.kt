package woowacourse.shopping.view.payment.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.view.payment.state.CouponUi
import woowacourse.shopping.view.payment.vm.PaymentViewModel

class CouponAdapter(
    private val viewModel: PaymentViewModel
): RecyclerView.Adapter<CouponViewHolder>() {
    private var couponItems: List<CouponUi> = emptyList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CouponViewHolder {
        return CouponViewHolder.of(parent)
    }

    override fun getItemCount(): Int = couponItems.size

    override fun onBindViewHolder(holder: CouponViewHolder, position: Int) {
        holder.bind(couponItems[position], viewModel)
    }

    fun updateItems(items: List<CouponUi>) {
        couponItems = items
        notifyDataSetChanged()
    }
}
