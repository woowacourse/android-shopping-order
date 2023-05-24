package woowacourse.shopping.data.datasource.cart

import woowacourse.shopping.data.mapper.toData
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.data.service.CartService
import woowacourse.shopping.data.service.ProductId
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.ProductCount
import woowacourse.shopping.domain.repository.CartRemoteRepository

class CartRemoteRepositoryImpl(private val cartService: CartService) : CartRemoteRepository {
    override fun getAllCartProduct(): List<CartProduct> {
        return cartService.getAllCartProduct().map { it.toDomain() }
    }

    override fun addCartProductByProductId(productId: ProductId) {
        cartService.addCartProductByProductId(productId)
    }

    override fun updateProductCountById(cartProductId: Int, count: ProductCount) {
        cartService.updateProductCountById(cartProductId, count.toData())
    }

    override fun deleteCartProductById(cartProductId: Int) {
        cartService.deleteCartProductById(cartProductId)
    }
}
