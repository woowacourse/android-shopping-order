package woowacourse.shopping.data.repository.okhttp

import woowacourse.shopping.data.mapper.toData
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.data.service.okhttp.cart.CartService
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.ProductCount
import woowacourse.shopping.domain.repository.CartProductId
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductId

class CartRepositoryImpl(private val service: CartService) : CartRepository {
    override fun getAllCartProducts(): List<CartProduct> {
        return service.getAllCartProduct().map { it.toDomain() }
    }

    override fun addCartProductByProductId(productId: ProductId) {
        service.addCartProductByProductId(productId)
    }

    override fun updateProductCountById(cartProductId: CartProductId, count: ProductCount) {
        service.updateProductCountById(cartProductId, count.toData())
    }

    override fun deleteCartProductById(cartProductId: CartProductId) {
        service.deleteCartProductById(cartProductId)
    }

    override fun findCartProductByProductId(productId: Int): CartProduct? {
        return service.findCartProductByProductId(productId)?.toDomain()
    }

    override fun increaseProductCountByProductId(productId: Int, addCount: ProductCount) {
        val cartProduct = findCartProductByProductId(productId) ?: run {
            addCartProductByProductId(productId)
            val newCartProduct = findCartProductByProductId(productId) ?: return
            return service.updateProductCountById(newCartProduct.id, addCount.toData())
        }

        val updatedCount = cartProduct.selectedCount + addCount
        service.updateProductCountById(cartProduct.id, updatedCount.toData())
    }
}
