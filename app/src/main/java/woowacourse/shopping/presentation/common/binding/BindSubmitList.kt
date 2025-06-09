package woowacourse.shopping.presentation.common.binding

import android.util.Log
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

@BindingAdapter("submitList")
fun <T> bindSubmitList(
    recyclerView: RecyclerView,
    items: List<T>?,
) {
    val baseAdapter = recyclerView.adapter
    if (baseAdapter !is ListAdapter<*, *>) {
        Log.w(
            "BindingAdapter",
            "Adapter is not a ListAdapter: ${baseAdapter?.javaClass?.simpleName}",
        )
        return
    }

    @Suppress("UNCHECKED_CAST")
    val listAdapter = baseAdapter as? ListAdapter<T, RecyclerView.ViewHolder>
    if (listAdapter == null) {
        Log.w("BindingAdapter", "ListAdapter type mismatch: ${baseAdapter.javaClass.simpleName}")
        return
    }

    listAdapter.submitList(items)
}
