package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.remote.dto.request.RequestOrderDto
import woowacourse.shopping.domain.model.CartProduct

object OrderMapper {
    fun CartProduct.toOrderItemDto(): RequestOrderDto.OrderItemDto {
        return RequestOrderDto.OrderItemDto(
            cartItemId = cartItem.id,
            orderCartItemName = product.name,
            orderCartItemPrice = product.price,
            orderCartItemImageUrl = product.imageUrl,
        )
    }
}
