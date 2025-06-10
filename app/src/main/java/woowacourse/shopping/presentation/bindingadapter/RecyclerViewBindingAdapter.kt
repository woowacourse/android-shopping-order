package woowacourse.shopping.presentation.bindingadapter

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

@BindingAdapter("submitList")
fun <T> bindRecyclerViewItems(
    view: RecyclerView,
    items: List<T>?,
) {
    val adapter = view.adapter
    if (adapter is ListAdapter<*, *>) {
        try {
            @Suppress("UNCHECKED_CAST")
            (adapter as ListAdapter<T, *>).submitList(items ?: emptyList())
        } catch (e: ClassCastException) {
            throw Exception("타입이 맞지 않습니다.")
        }
    }
}
