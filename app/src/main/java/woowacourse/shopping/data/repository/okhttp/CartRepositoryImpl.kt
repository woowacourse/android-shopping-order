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
    override fun getAllCartProducts(onSuccess: (List<CartProduct>) -> Unit, onFailure: () -> Unit) {
        service.getAllCartProduct().map { it.toDomain() }
    }

    override fun addCartProductByProductId(
        productId: ProductId,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    ) {
        service.addCartProductByProductId(productId)
    }

    override fun updateProductCountById(
        cartProductId: CartProductId,
        count: ProductCount,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    ) {
        service.updateProductCountById(cartProductId, count.toData())
    }

    override fun deleteCartProductById(
        cartProductId: CartProductId,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    ) {
        service.deleteCartProductById(cartProductId)
    }

    override fun findCartProductByProductId(
        productId: ProductId,
        onSuccess: (CartProduct) -> Unit,
        onFailure: () -> Unit,
    ) {
        service.findCartProductByProductId(productId)?.toDomain()
    }

    override fun increaseProductCountByProductId(productId: Int, addCount: ProductCount) {
//        val cartProduct = findCartProductByProductId(productId) ?: run {
//            addCartProductByProductId(productId)
//            val newCartProduct = findCartProductByProductId(productId) ?: return
//            return service.updateProductCountById(newCartProduct.id, addCount.toData())
//        }
//
//        val updatedCount = cartProduct.selectedCount + addCount
//        service.updateProductCountById(cartProduct.id, updatedCount.toData())
    }
}
