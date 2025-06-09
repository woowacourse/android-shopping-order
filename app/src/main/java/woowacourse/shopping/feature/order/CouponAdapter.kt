package woowacourse.shopping.feature.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemCouponBinding
import woowacourse.shopping.domain.model.Coupon

class CouponAdapter(
    private val onCouponClick: (Coupon) -> Unit,
) : RecyclerView.Adapter<CouponAdapter.CouponViewHolder>() {

    private var coupons: List<Coupon> = emptyList()
    private var selectedCoupon: Coupon? = null

    inner class CouponViewHolder(
        private val binding: ItemCouponBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(coupon: Coupon) {

            binding.tvCouponExpiry.text = "만료일: ${coupon.expirationDate?:"없음"}"

            binding.checkBoxCoupon.isChecked = (coupon == selectedCoupon)

            binding.checkBoxCoupon.setOnClickListener {
                onCouponClick(coupon)
            }
            binding.root.setOnClickListener {
                onCouponClick(coupon)
            }
            binding.tvCouponName.text = coupon.description
            val context = binding.root.context

            binding.tvCouponDiscount.text = context.getString(R.string.minimum_date, coupon.minimumAmount?:0)

            binding.root.setOnClickListener { onCouponClick(coupon) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CouponViewHolder {
        val binding = ItemCouponBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false,
        )
        return CouponViewHolder(binding)
    }

    override fun getItemCount(): Int = coupons.size

    override fun onBindViewHolder(holder: CouponViewHolder, position: Int) {
        holder.bind(coupons[position])
    }

    fun submitList(newCoupons: List<Coupon>, selected: Coupon?) {
        coupons = newCoupons
        selectedCoupon = selected
        notifyDataSetChanged()
    }

}