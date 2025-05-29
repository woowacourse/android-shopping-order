package woowacourse.shopping.data

import woowacourse.shopping.data.datasource.remote.ProductDataSource
import woowacourse.shopping.data.entity.CartEntity
import woowacourse.shopping.domain.model.CartItem

class CartItemMapper(
    private val productDataSource: ProductDataSource,
) {
//    fun toDomain(data: CartEntity): CartItem {
//        productDataSource.fetchProductById(data.productId){ result ->
//            result.
//        }
//        return CartItem(product, data.quantity)
//    }


    fun toData(domain: CartItem): CartEntity =
        CartEntity(
            productId = domain.product.productId,
            quantity = 1,
        )
}
