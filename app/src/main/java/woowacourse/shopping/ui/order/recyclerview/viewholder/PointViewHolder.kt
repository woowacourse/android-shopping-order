package woowacourse.shopping.ui.order.recyclerview.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemPointBinding
import woowacourse.shopping.model.Point
import woowacourse.shopping.ui.order.recyclerview.ListItem

class PointViewHolder(
    parent: ViewGroup,
) : BaseViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_point, parent, false)
) {
    private val binding: ItemPointBinding = ItemPointBinding.bind(itemView)

    override fun bind(item: ListItem) {
        binding.availablePoint = item as? Point
    }
}
