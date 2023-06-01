package woowacourse.shopping.mapper

import com.example.domain.model.OrderMinInfoItem
import com.example.domain.model.Price
import woowacourse.shopping.model.OrderMinInfoItemUiModel

fun OrderMinInfoItem.toPresentation(): OrderMinInfoItemUiModel {
    return OrderMinInfoItemUiModel(
        id,
        mainProductName,
        mainProductImage,
        extraProductCount,
        date,
        price.value
    )
}

fun OrderMinInfoItemUiModel.toDomain(): OrderMinInfoItem {
    return OrderMinInfoItem(
        id,
        mainProductName,
        mainProductImage,
        extraProductCount,
        date,
        Price(price)
    )
}
