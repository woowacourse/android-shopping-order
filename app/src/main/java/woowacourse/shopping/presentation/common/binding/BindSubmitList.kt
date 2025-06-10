package woowacourse.shopping.presentation.common.binding

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

@BindingAdapter("submitList")
fun <T> bindSubmitList(
    recyclerView: RecyclerView,
    items: List<T>?,
) {
    val baseAdapter = recyclerView.adapter

    require(baseAdapter is ListAdapter<*, *>) {
        "RecyclerView에 설정된 어댑터는 ListAdapter여야 합니다. (현재: ${baseAdapter?.javaClass?.simpleName})"
    }

    @Suppress("UNCHECKED_CAST")
    val listAdapter = baseAdapter as? ListAdapter<T, RecyclerView.ViewHolder>

    requireNotNull(listAdapter) {
        "ListAdapter의 제네릭 타입이 일치하지 않습니다. (현재: ${baseAdapter.javaClass.simpleName})"
    }

    listAdapter.submitList(items)
}
