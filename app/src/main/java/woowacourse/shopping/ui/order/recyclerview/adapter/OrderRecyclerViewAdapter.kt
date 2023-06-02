package woowacourse.shopping.ui.order.recyclerview.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.model.PointModel
import woowacourse.shopping.ui.order.recyclerview.ListItem
import woowacourse.shopping.ui.order.recyclerview.OrderViewType
import woowacourse.shopping.ui.order.recyclerview.viewholder.BaseViewHolder
import woowacourse.shopping.ui.order.recyclerview.viewholder.OrderViewHolder
import woowacourse.shopping.ui.order.recyclerview.viewholder.PointViewHolder

class OrderRecyclerViewAdapter(
    private val items: MutableList<ListItem> = mutableListOf(),
    private val onApplyPoint: (discountAppliedPoint: PointModel) -> Unit,
) : RecyclerView.Adapter<BaseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (OrderViewType.of(viewType)) {
            OrderViewType.ORDER -> OrderViewHolder(parent)
            OrderViewType.POINT -> PointViewHolder(parent, onApplyPoint)
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int = items[position].viewType

    fun update(newItems: List<ListItem>) {
        items.clear()
        items.addAll(newItems)
        notifyItemRangeInserted(0, newItems.size)
    }
}
