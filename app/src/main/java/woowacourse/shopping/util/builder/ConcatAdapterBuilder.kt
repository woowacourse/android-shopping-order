package woowacourse.shopping.util.builder

import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder

fun notIsolatedViewTypeConcatAdapter(block: ConcatAdapter.() -> Unit): ConcatAdapter =
    ConcatAdapter(notIsolatedViewTypeConfig).apply(block)

fun <T : ViewHolder> ConcatAdapter.add(adapter: Adapter<T>) {
    addAdapter(adapter)
}

val notIsolatedViewTypeConfig: ConcatAdapter.Config
    get() = ConcatAdapter.Config.Builder()
        .setIsolateViewTypes(false)
        .build()
