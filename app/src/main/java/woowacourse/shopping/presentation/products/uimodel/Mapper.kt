package woowacourse.shopping.presentation.products.uimodel

import com.example.domain.model.RecentProduct

fun RecentProduct.toRecentProductUiModel() = RecentProductUiModel(product, product.imageUrl, product.name)

fun List<RecentProduct>.toRecentProdutUiModels() = map { it.toRecentProductUiModel() }
