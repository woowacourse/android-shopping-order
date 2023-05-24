package woowacourse.shopping.mapper

import com.example.domain.model.RecentProduct
import woowacourse.shopping.model.RecentProductUiModel

fun RecentProduct.toPresentation(): RecentProductUiModel =
    RecentProductUiModel(productId, imageUrl, dateTime)

fun RecentProductUiModel.toDomain(): RecentProduct =
    RecentProduct(productId, imageUrl, dateTime)
