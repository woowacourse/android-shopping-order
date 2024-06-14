package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.remote.model.dto.ContentDto
import woowacourse.shopping.data.database.entity.CartItemEntity
import woowacourse.shopping.domain.model.CartItem

fun CartItemEntity.toDomainModel(): CartItem {
    return CartItem(
        id = this.id,
        productId = this.productId,
        productName = this.productName,
        price = this.price,
        imgUrl = this.imgUrl,
        quantity = this.quantity,
    )
}

fun ContentDto.toDomainModel(): CartItem {
    return CartItem(
        id = this.id,
        productId = this.product.id,
        productName = this.product.name,
        price = this.product.price,
        imgUrl = this.product.imageUrl,
        quantity = this.quantity,
    )
}
