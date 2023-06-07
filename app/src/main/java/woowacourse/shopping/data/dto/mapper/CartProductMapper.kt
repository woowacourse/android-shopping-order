package woowacourse.shopping.data.dto.mapper

import woowacourse.shopping.data.cart.response.GetCartProductResponse
import woowacourse.shopping.data.dto.mapper.ProductMapper.toDomain
import woowacourse.shopping.data.dto.mapper.ProductMapper.toEntity
import woowacourse.shopping.domain.CartProduct

object CartProductMapper: Mapper<CartProduct, GetCartProductResponse> {
    override fun CartProduct.toEntity(): GetCartProductResponse {
        return GetCartProductResponse(
            id,
            quantity,
            product.toEntity()
        )
    }

    override fun GetCartProductResponse.toDomain(): CartProduct {
        return CartProduct(
            id,
            quantity,
            isChecked = true,
            product.toDomain()
        )
    }
}