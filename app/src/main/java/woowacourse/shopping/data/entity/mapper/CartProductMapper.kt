package woowacourse.shopping.data.entity.mapper

import woowacourse.shopping.data.entity.CartProductEntity
import woowacourse.shopping.data.entity.mapper.ProductMapper.toDomain
import woowacourse.shopping.data.entity.mapper.ProductMapper.toEntity
import woowacourse.shopping.domain.CartProduct

object CartProductMapper: Mapper<CartProduct, CartProductEntity> {
    override fun CartProduct.toEntity(): CartProductEntity {
        return CartProductEntity(
            id,
            quantity,
            product.toEntity()
        )
    }

    override fun CartProductEntity.toDomain(): CartProduct {
        return CartProduct(
            id,
            quantity,
            isChecked = true,
            product.toDomain()
        )
    }
}