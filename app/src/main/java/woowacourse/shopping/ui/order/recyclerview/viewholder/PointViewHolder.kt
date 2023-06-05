package woowacourse.shopping.ui.order.recyclerview.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemPointBinding
import woowacourse.shopping.model.PointModel
import woowacourse.shopping.ui.order.recyclerview.ListItem
import woowacourse.shopping.util.extension.setOnSingleClickListener

class PointViewHolder(
    parent: ViewGroup,
    onApplyPoint: (discountAppliedPoint: PointModel) -> Unit,
) : BaseViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_point, parent, false)
) {
    private val binding: ItemPointBinding = ItemPointBinding.bind(itemView)

    init {
        binding.pointApplyButton.setOnSingleClickListener {
            onApplyPoint(PointModel(binding.pointEditText.point))
        }
    }

    override fun bind(maxPoint: ListItem) {
        binding.maxAvailablePoint = maxPoint as? PointModel
    }
}
