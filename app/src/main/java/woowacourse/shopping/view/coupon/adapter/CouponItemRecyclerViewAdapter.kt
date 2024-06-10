package woowacourse.shopping.view.coupon.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCouponBinding
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.view.coupon.OnClickCoupon
import woowacourse.shopping.view.coupon.adapter.viewholder.CouponViewHolder

class CouponItemRecyclerViewAdapter(
    private val onclickCoupon: OnClickCoupon,
) : RecyclerView.Adapter<CouponViewHolder>() {
    private val coupons = mutableListOf<Coupon>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CouponViewHolder {
        return CouponViewHolder(
            ItemCouponBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), onclickCoupon
        )
    }

    override fun onBindViewHolder(holder: CouponViewHolder, position: Int) {
        val item = coupons[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = coupons.size

    fun submitList(it: List<Coupon>?): List<Coupon>? {
        if (it != null) {
            coupons.clear()
            coupons.addAll(it)
            notifyDataSetChanged()
        }
        return it
    }
}
