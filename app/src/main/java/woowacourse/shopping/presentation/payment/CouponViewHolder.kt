package woowacourse.shopping.presentation.payment

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.data.payment.model.Coupon
import woowacourse.shopping.databinding.ItemCouponBinding

class CouponViewHolder(
    private val binding: ItemCouponBinding,
    private val lifecycleOwner: LifecycleOwner,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(
        coupon: Coupon,
        viewModel: PaymentViewModel,
    ) {
        Log.d("alsong", "뷰홀더: $coupon")
        binding.coupon = coupon
        binding.vm = viewModel
        binding.lifecycleOwner = lifecycleOwner
    }
}
