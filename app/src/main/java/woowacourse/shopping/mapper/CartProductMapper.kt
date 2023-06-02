package woowacourse.shopping.mapper

import com.example.domain.model.CartProduct
import woowacourse.shopping.data.remote.request.CartProductDTO
import woowacourse.shopping.model.CartProductUIModel

fun CartProduct.toUIModel(): CartProductUIModel {
    return CartProductUIModel(
        id = this.id,
        quantity = this.quantity,
        product = this.product.toUIModel(),
    )
}

fun CartProductUIModel.toDomain(): CartProduct {
    return CartProduct(
        id = this.id,
        quantity = this.quantity,
        product = this.product.toDomain(),
    )
}

fun CartProductDTO.toDomain(): CartProduct {
    return CartProduct(
        id = this.id,
        quantity = this.quantity,
        product = this.productDto.toDomain(),
    )
}
