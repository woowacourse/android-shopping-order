package woowacourse.shopping.data.cart

import woowacourse.shopping.CartProductInfoList
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.repository.CartRepository

class CartRepositoryImpl constructor(
    private val cartRemoteDataSource: CartRemoteDataSource,
) : CartRepository {
    override fun putProductInCart(productId: Int) {
        cartRemoteDataSource.addProduct(productId)
    }

    override fun deleteCartProductId(cartId: Int) {
        cartRemoteDataSource.deleteCartProduct(cartId)
    }

    override fun updateCartProductCount(cartId: Int, count: Int) {
        cartRemoteDataSource.updateProductCount(
            cartId = cartId,
            count = count
        )
    }

    override fun getAllCartProductsInfo(): CartProductInfoList {
        val cartDataModels = cartRemoteDataSource.getAllCartProductsInfo()
        return CartProductInfoList(
            cartDataModels.map {
                it.toDomain()
            },
        )
    }

    override fun getCartIdByProductId(productId: Int): Int {
        val cartDataModels = cartRemoteDataSource.getAllCartProductsInfo()
        val cartProductInfoList = CartProductInfoList(cartDataModels.map { it.toDomain() })
        return cartProductInfoList.findCartIdByProductId(productId)
    }
}
