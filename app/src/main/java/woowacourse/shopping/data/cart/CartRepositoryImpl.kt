package woowacourse.shopping.data.cart

import woowacourse.shopping.data.product.ProductDataSource
import woowacourse.shopping.model.CartProduct
import woowacourse.shopping.model.Product
import woowacourse.shopping.presentation.mapper.toPresentation
import woowacourse.shopping.presentation.model.CartProductModel
import woowacourse.shopping.presentation.model.UnCheckableCartProductModel

class CartRepositoryImpl(
    private val cartDataSource: CartDataSource,
    private val productDataSource: ProductDataSource,
) : CartRepository {

    override fun findProductById(productId: Long, callback: (Product) -> Unit) {
        productDataSource.findProductById(productId) { productDetail ->
            callback(productDetail.product.toDomainProduct())
        }
    }

    override fun getCartProducts(callback: (List<CartProduct>) -> Unit) {
        cartDataSource.getAllCartProducts { cartProducts ->
            callback(cartProducts.map { it.toDomainCartProduct() })
        }
    }

//    override fun getCartProduct(productId: Long): CartProduct {
//        val cartProduct = cartDataSource.getCartProduct(productId)
//        return CartProduct(
//            cartProduct.id,
//            cartProduct.product.toDomainProduct(),
//            cartProduct.quantity,
//            true,
//        )
//    }

    override fun deleteCartProduct(cartId: Long, callback: () -> Unit) {
        cartDataSource.deleteCartProduct(cartId, callback)
    }

    override fun insertCartProduct(productId: Long, count: Int, callback: () -> Unit) {
        cartDataSource.insertCartProduct(productId) { cartId ->
            if (count > 1) {
                cartDataSource.updateCartProduct(cartId, count) {
                    callback()
                    return@updateCartProduct
                }
            }
        }
        callback()
    }

    override fun updateCartProductCount(cartId: Long, count: Int, callback: () -> Unit) {
        if (count <= 0) {
            cartDataSource.deleteCartProduct(cartId) {
                callback()
                return@deleteCartProduct
            }
        }
        cartDataSource.updateCartProduct(cartId, count) {
            callback()
        }
    }

    override fun getProductsByRange(
        lastProductId: Long,
        pageItemCount: Int,
        callback: (List<CartProductModel>, Boolean) -> Unit,
    ) {
        productDataSource.getProductsWithRange(
            lastProductId,
            pageItemCount,
        ) { productDetails, isLast ->
            val cartProductModels = productDetails.map {
                if (it.cartItem == null) {
                    UnCheckableCartProductModel(
                        0,
                        it.product.toDomainProduct().toPresentation(),
                        0,
                    )
                } else {
                    UnCheckableCartProductModel(
                        it.cartItem.id,
                        it.product.toDomainProduct().toPresentation(),
                        it.cartItem.quantity,
                    )
                }
            }
            callback(cartProductModels, isLast)
        }
    }
}
