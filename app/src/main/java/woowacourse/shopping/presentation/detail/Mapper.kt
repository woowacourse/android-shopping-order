package woowacourse.shopping.presentation.detail

import com.example.domain.model.RecentProduct

fun RecentProduct.toLastRecentProductUiModel(): LastRecentProductUiModel = LastRecentProductUiModel(product)
