package woowacourse.shopping.mapper

import com.example.domain.model.OrderProduct
import woowacourse.shopping.data.remote.request.OrderProductDTO
import woowacourse.shopping.model.OrderProductUIModel

fun OrderProduct.toUIModel(): OrderProductUIModel {
    return OrderProductUIModel(
        product = this.product.toUIModel(),
        quantity = this.quantity,
    )
}

fun OrderProductUIModel.toDomain(): OrderProduct {
    return OrderProduct(
        product = this.product.toDomain(),
        quantity = this.quantity,
    )
}

fun OrderProductDTO.toDomain(): OrderProduct {
    return OrderProduct(
        product = this.product.toDomain(),
        quantity = this.quantity,
    )
}
