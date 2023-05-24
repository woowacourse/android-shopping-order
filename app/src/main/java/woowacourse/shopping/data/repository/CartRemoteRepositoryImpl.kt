package woowacourse.shopping.data.repository

import woowacourse.shopping.data.mapper.toData
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.data.service.CartService
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.ProductCount
import woowacourse.shopping.domain.repository.CartProductId
import woowacourse.shopping.domain.repository.CartRemoteRepository
import woowacourse.shopping.domain.repository.ProductId

class CartRemoteRepositoryImpl(private val service: CartService) : CartRemoteRepository {
    override fun getAllCartProduct(): List<CartProduct> {
        return service.getAllCartProduct().map { it.toDomain() }
    }

    override fun addCartProductByProductId(productId: ProductId) {
        service.addCartProductByProductId(productId)
    }

    override fun updateProductCountById(cartProductId: CartProductId, count: ProductCount) {
        service.updateProductCountById(cartProductId, count.toData())
    }

    override fun deleteCartProductById(cartProductId: CartProductId) {
        TODO("Not yet implemented")
    }
}
