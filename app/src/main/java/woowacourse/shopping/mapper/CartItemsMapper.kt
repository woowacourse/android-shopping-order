package woowacourse.shopping.mapper

import com.example.domain.model.CartItems
import woowacourse.shopping.model.CartItemsUIModel

fun CartItems.toUIModel(): CartItemsUIModel {
    return CartItemsUIModel(
        getToList().map { it.toUIModel() },
        getPrice(),
        getSize(),
    )
}

fun CartItemsUIModel.toDomain(): CartItems {
    return CartItems(
        cartProducts.map { it.toDomain() },
    )
}
