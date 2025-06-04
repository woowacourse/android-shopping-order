package woowacourse.shopping.feature.cart.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemGoodsBinding
import woowacourse.shopping.databinding.ItemGoodsSkeletonBinding
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.feature.QuantityChangeListener
import woowacourse.shopping.feature.goods.adapter.vertical.GoodsDiffCallback
import woowacourse.shopping.feature.goods.adapter.vertical.GoodsListItem
import woowacourse.shopping.feature.goods.adapter.vertical.GoodsViewHolder
import woowacourse.shopping.feature.goods.adapter.vertical.SkeletonViewHolder

class RecommendAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val externalQuantityChange: QuantityChangeListener,
) : ListAdapter<GoodsListItem, RecyclerView.ViewHolder>(GoodsDiffCallback()) {
    fun showSkeleton(count: Int = 10) {
        val skeletonItems = List(count) { GoodsListItem.Skeleton }
        submitList(skeletonItems)
    }

    fun setRecommendItem(goodsItem: List<CartItem>) {
        val newItems = goodsItem.map { GoodsListItem.GoodsData(it) }
        submitList(newItems)
    }

    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is GoodsListItem.Skeleton -> TYPE_SKELETON
            is GoodsListItem.GoodsData -> TYPE_RECOMMEND_ITEM
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            TYPE_SKELETON -> {
                val binding = ItemGoodsSkeletonBinding.inflate(inflater, parent, false)
                SkeletonViewHolder(binding)
            }
            TYPE_RECOMMEND_ITEM -> {
                val binding = ItemGoodsBinding.inflate(inflater, parent, false)
                binding.quantityChangeListener = externalQuantityChange
                GoodsViewHolder(binding)
            }

            else -> throw IllegalArgumentException("알 수 없는 뷰 타입: $viewType")
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        val screenWith = holder.itemView.context.resources.displayMetrics.widthPixels
        holder.itemView.layoutParams =
            holder.itemView.layoutParams.apply {
                width = (screenWith * 0.4f).toInt()
            }
        when (val item = getItem(position)) {
            is GoodsListItem.Skeleton -> {}
            is GoodsListItem.GoodsData -> {
                val goodsHolder = holder as GoodsViewHolder
                goodsHolder.bind(item.goodsItem)
            }
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: List<Any>,
    ) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            val payload = payloads[0]
            if (payload == QUANTITY_CHANGED_PAYLOAD && holder is GoodsViewHolder) {
                val item = getItem(position) as GoodsListItem.GoodsData
                holder.bind(item.goodsItem)
                holder.binding.cartItem = item.goodsItem
                holder.binding.quantitySelector.cartItem = item.goodsItem
            }
        }
    }

    companion object {
        private const val QUANTITY_CHANGED_PAYLOAD = "quantity_changed"

        private const val TYPE_SKELETON = 0
        private const val TYPE_RECOMMEND_ITEM = 1
    }
}
