package woowacourse.shopping.feature.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCouponBinding
import java.time.format.DateTimeFormatter

class CouponAdapter(
    private val onCouponClick: (Coupon) -> Unit,
) : RecyclerView.Adapter<CouponAdapter.CouponViewHolder>() {

    private var coupons: List<Coupon> = emptyList()
    private var selectedCoupon: Coupon? = null

    inner class CouponViewHolder(
        private val binding: ItemCouponBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(coupon: Coupon) {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

            binding.tvCouponExpiry.text = "만료일: ${coupon.expiry.format(formatter)}"

            binding.checkBoxCoupon.isChecked = (coupon == selectedCoupon)

            binding.checkBoxCoupon.setOnClickListener {
                onCouponClick(coupon)
            }
            binding.root.setOnClickListener {
                onCouponClick(coupon)
            }

            when (coupon) {
                is Coupon.Fixed5000 -> {
                    binding.tvCouponName.text = "5,000원 할인 쿠폰"
                    binding.tvCouponDiscount.text = "100,000원 이상 구매 시 5,000원 할인"
                }
                is Coupon.BOGO -> {
                    binding.tvCouponName.text = "2+1 무료 쿠폰"
                    binding.tvCouponDiscount.text = "3개 이상 구매 시 1개 무료 (최고가 기준)"
                }
                is Coupon.FreeShipping -> {
                    binding.tvCouponName.text = "무료 배송 쿠폰"
                    binding.tvCouponDiscount.text = "50,000원 이상 구매 시 배송비 무료"
                }
                is Coupon.MiracleSale -> {
                    binding.tvCouponName.text = "미라클모닝 30% 할인"
                    binding.tvCouponDiscount.text = "오전 4~7시 30% 할인"
                }
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

}