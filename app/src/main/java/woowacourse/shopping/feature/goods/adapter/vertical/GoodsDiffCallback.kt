package woowacourse.shopping.feature.goods.adapter.vertical

import androidx.recyclerview.widget.DiffUtil

class GoodsDiffCallback : DiffUtil.ItemCallback<GoodsListItem>() {
    override fun areItemsTheSame(
        oldItem: GoodsListItem,
        newItem: GoodsListItem,
    ): Boolean =
        when {
            oldItem is GoodsListItem.Skeleton && newItem is GoodsListItem.Skeleton -> true
            oldItem is GoodsListItem.GoodsData && newItem is GoodsListItem.GoodsData ->
                oldItem.goodsItem.goods.id == newItem.goodsItem.goods.id

            else -> false
        }

    override fun areContentsTheSame(
        oldItem: GoodsListItem,
        newItem: GoodsListItem,
    ): Boolean = oldItem == newItem
}
