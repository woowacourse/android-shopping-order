package woowacourse.shopping.presentation.model

data class DisplayModel<T>(
    val data: T,
    val isSelected: Boolean = false,
)
