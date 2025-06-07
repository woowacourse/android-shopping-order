package woowacourse.shopping.feature.cart.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCouponBinding
import woowacourse.shopping.databinding.ItemCouponSkeletonBinding
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.feature.cart.CartViewModel

class CouponAdapter(
    private val viewModel: CartViewModel,
) : ListAdapter<CouponListItem, RecyclerView.ViewHolder>(CouponDiffCallback()) {
    fun showSkeleton(count: Int = 2) {
        val skeletonItems = List(count) { CouponListItem.Skeleton }
        submitList(skeletonItems)
    }

    fun setCouponItem(couponItem: List<Coupon>) {
        val newItems = couponItem.map { CouponListItem.CouponData(it) }
        submitList(newItems)
    }

    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is CouponListItem.Skeleton -> TYPE_SKELETON
            is CouponListItem.CouponData -> TYPE_COUPON
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            TYPE_SKELETON -> {
                val binding = ItemCouponSkeletonBinding.inflate(inflater, parent, false)
                CouponSkeletonViewHolder(binding)
            }
            TYPE_COUPON -> {
                val binding = ItemCouponBinding.inflate(inflater, parent, false)
                binding.viewModel = viewModel
                CouponViewHolder(binding)
            }

            else -> throw IllegalArgumentException("알 수 없는 뷰 타입: $viewType")
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        when (val item = getItem(position)) {
            is CouponListItem.CouponData -> {
                val couponHolder = holder as CouponViewHolder
                couponHolder.bind(item.couponItem)
            }
            CouponListItem.Skeleton -> {}
        }
    }

    companion object {
        private const val TYPE_SKELETON = 0
        private const val TYPE_COUPON = 1
    }
}
