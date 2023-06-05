package woowacourse.shopping.ui.order.recyclerview.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.ui.order.recyclerview.ListItem

abstract class BaseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(item: ListItem)
}
