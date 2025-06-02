package woowacourse.shopping.domain

import android.view.View

data class LoadingState(
    val isLoading: Boolean,
    val shimmerVisibility: Int,
    val recyclerViewVisibility: Int,
) {
    companion object {
        fun loading(): LoadingState = LoadingState(true, View.VISIBLE, View.GONE)

        fun loaded(): LoadingState = LoadingState(false, View.GONE, View.VISIBLE)

        fun refreshing(): LoadingState = LoadingState(true, View.GONE, View.VISIBLE)
    }
}
