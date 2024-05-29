package woowacourse.shopping.presentation.ui.shoppingcart

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("currentPage", "last", "isLoading")
fun TextView.bindPageNavigationTextVisible(
    currentPage: Int?,
    last: Boolean?,
    isLoading: Boolean?,
) {
    if (currentPage == null || last == null || isLoading == null) return
    if (isLoading == true) {
        this.visibility = View.GONE
    } else {
        if (currentPage == 0 && last == true) {
            this.visibility = View.GONE
        } else {
            this.visibility = View.VISIBLE
        }
    }
}
