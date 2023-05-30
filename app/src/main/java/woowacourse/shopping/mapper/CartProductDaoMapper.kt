package woowacourse.shopping.mapper

import com.example.domain.model.CartProduct
import woowacourse.shopping.data.dto.CartProductDto

fun CartProductDto.toDomain(): CartProduct =
    CartProduct(cartId, product.toDomain(), count, true)
