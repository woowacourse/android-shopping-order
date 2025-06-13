package woowacourse.shopping.feature.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemCouponBinding
import woowacourse.shopping.domain.model.Coupon

class OrderAdapter(
    private val onCouponClick: (Coupon) -> Unit,
) : RecyclerView.Adapter<OrderAdapter.CouponViewHolder>() {

    private var coupons: List<Coupon> = emptyList()
    private var selectedCoupon: Coupon? = null

    inner class CouponViewHolder(
        private val binding: ItemCouponBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(coupon: Coupon) {

            binding.coupon = coupon

            binding.checkBoxCoupon.isChecked = (coupon == selectedCoupon)

            binding.checkBoxCoupon.setOnClickListener {
                onCouponClick(coupon)
            }
            binding.root.setOnClickListener {
                onCouponClick(coupon)
            }

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

    fun submitCoupon(selected: Coupon?) {
        val previousSelected = selectedCoupon
        selectedCoupon = selected

        previousSelected?.let {
            notifyItemChanged(coupons.indexOf(it))
        }
        selectedCoupon?.let {
            notifyItemChanged(coupons.indexOf(it))
        }
    }

}