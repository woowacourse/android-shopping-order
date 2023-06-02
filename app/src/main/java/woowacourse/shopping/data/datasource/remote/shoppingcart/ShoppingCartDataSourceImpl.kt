package woowacourse.shopping.data.datasource.remote.shoppingcart

import retrofit2.Call
import woowacourse.shopping.data.datasource.local.AuthInfoDataSource
import woowacourse.shopping.data.datasource.retrofit.ServicePool
import woowacourse.shopping.data.remote.request.CartProductDTO

class ShoppingCartDataSourceImpl(private val authInfoDataSource: AuthInfoDataSource) :
    ShoppingCartDataSource {

    private val token = authInfoDataSource.getAuthInfo() ?: throw IllegalArgumentException()

    override fun getAllProductInCart(): Call<List<CartProductDTO>> {
        return ServicePool.shoppingCartService.getAllProductInCart(token)
    }

    override fun postProductToCart(productId: Long): Call<Void> {
        return ServicePool.shoppingCartService.postProductToCart(token, productId)
    }

    override fun patchProductCount(cartItemId: Long, quantity: Int): Call<Unit> {
        return ServicePool.shoppingCartService.patchProductCount(token, cartItemId, quantity)
    }

    override fun deleteProductInCart(productId: Long): Call<Unit> {
        return ServicePool.shoppingCartService.deleteProductInCart(token, productId)
    }

    companion object {
        private const val CART_PATH = "/cart-items"
        private const val POST_PRODUCT_TO_CART = "/cart-items"
    }
}
