package woowacourse.shopping.ui.model.mapper

import woowacourse.shopping.ui.model.CartProductModel
import woowacourse.shopping.ui.model.mapper.ProductMapper.toDomain
import woowacourse.shopping.ui.model.mapper.ProductMapper.toView
import woowacourse.shopping.domain.CartProduct

object CartProductMapper : Mapper<CartProduct, CartProductModel> {
    override fun CartProduct.toView(): CartProductModel {
        return CartProductModel(
            id,
            quantity,
            isChecked,
            product.toView()
        )
    }

    override fun CartProductModel.toDomain(): CartProduct {
        return CartProduct(
            id,
            quantity,
            isChecked,
            product.toDomain()
        )
    }
}
