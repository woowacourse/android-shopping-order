package woowacourse.shopping.domain

import android.view.View

data class LoadingState(
    val isLoading: Boolean,
    val shimmerVisibility: Int,
    val recyclerViewVisibility: Int,
) {
    companion object {
        fun loading(): LoadingState {
            return LoadingState(true, View.VISIBLE, View.GONE)
        }

        fun loaded(): LoadingState {
            return LoadingState(false, View.GONE, View.VISIBLE)
        }
    }
}
