package woowacourse.shopping.remote.mapper

import woowacourse.shopping.data.model.remote.CartDto
import woowacourse.shopping.remote.model.response.CartResponse

fun CartResponse.toData(): CartDto {
    return CartDto(
        id = this.id,
        quantity = this.quantity,
        product = this.product.toData(),
    )
}
