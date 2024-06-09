package woowacourse.shopping.presentation.payment

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.data.payment.model.Coupon
import woowacourse.shopping.databinding.ItemCouponBinding

class CouponAdapter(
    private val viewModel: PaymentViewModel,
    private val lifecycleOwner: LifecycleOwner,
) : RecyclerView.Adapter<CouponViewHolder>() {
    private val data: MutableList<Coupon> = mutableListOf()

//    private val data: List<Coupon> get() {
//        return viewModel.coupons.value?.map { Coupon.of(it) } ?: emptyList()
//    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CouponViewHolder {
        val binding =
            ItemCouponBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CouponViewHolder(binding, lifecycleOwner)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(
        holder: CouponViewHolder,
        position: Int,
    ) {
        Log.d("alsong", "onBindViewHolder: $data")
        holder.bind(data[position], viewModel)
    }

    fun setData(coupons: List<Coupon>) {
        data.clear()
        data.addAll(coupons)
        notifyDataSetChanged()
    }
}
