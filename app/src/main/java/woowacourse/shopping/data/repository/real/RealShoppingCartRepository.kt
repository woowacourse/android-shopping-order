package woowacourse.shopping.data.repository.real

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.remote.dto.cart.CartItemQuantityDto
import woowacourse.shopping.data.remote.dto.cart.CartItemResponse
import woowacourse.shopping.data.remote.source.CartItemDataSourceImpl
import woowacourse.shopping.data.source.CartItemDataSource
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.CartItemCounter
import woowacourse.shopping.domain.model.CartItemResult
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.UpdateCartItemResult
import woowacourse.shopping.domain.model.UpdateCartItemType
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.utils.DtoMapper.toCartItem
import woowacourse.shopping.utils.exception.LatchUtils.awaitOrThrow
import woowacourse.shopping.utils.exception.NoSuchDataException
import java.util.concurrent.CountDownLatch

class RealShoppingCartRepository(
    private val cartItemDataSource: CartItemDataSource = CartItemDataSourceImpl(),
): ShoppingCartRepository {
    override fun addCartItem(product: Product) {
        val latch = CountDownLatch(1)
        var exception: Exception? = null

        cartItemDataSource.addCartItem(product.id.toInt(),product.cartItemCounter.itemCount)
            .enqueue(object : Callback<Unit>{
                override fun onResponse(p0: Call<Unit>, response: Response<Unit>) {
                    if(!response.isSuccessful){
                        exception = NoSuchDataException()
                    }
                    latch.countDown()
                }

                override fun onFailure(p0: Call<Unit>, t: Throwable) {
                    exception = Exception(t.message)
                    latch.countDown()
                }

            })
        latch.awaitOrThrow(exception)
    }

    override fun loadPagingCartItems(offset: Int, pagingSize: Int): List<CartItem> {
        val latch = CountDownLatch(1)
        var cartItems: List<CartItem>? = null
        var exception: Exception? = null

        cartItemDataSource.loadCartItems(page = offset, size = pagingSize)
            .enqueue(object : Callback<CartItemResponse>{
                override fun onResponse(
                    p0: Call<CartItemResponse>,
                    response: Response<CartItemResponse>
                ) {
                    cartItems = response.body()?.cartItemDto?.map { it.toCartItem() }
                    latch.countDown()
                }

                override fun onFailure(p0: Call<CartItemResponse>, t: Throwable) {
                    exception = Exception(t.message)
                    latch.countDown()
                }

            })
        latch.awaitOrThrow(exception)
        return cartItems ?: throw NoSuchDataException()
    }

    override fun deleteCartItem(itemId: Long) {
        val latch = CountDownLatch(1)
        var exception: Exception? = null

        cartItemDataSource.deleteCartItem(itemId.toInt())
            .enqueue(object : Callback<Unit>{
                override fun onResponse(p0: Call<Unit>, response: Response<Unit>) {
                    if (!response.isSuccessful){
                        exception = NoSuchDataException()
                    }
                    latch.countDown()
                }

                override fun onFailure(p0: Call<Unit>, t: Throwable) {
                    exception = Exception(t.message)
                    latch.countDown()
                }

            })
        latch.awaitOrThrow(exception)
    }

    override fun getCartItemResultFromProductId(productId: Long): CartItemResult {
        //TODO 수정
        return CartItemResult(0, CartItemCounter())
    }

    override fun updateCartItem(
        productId: Long,
        updateCartItemType: UpdateCartItemType
    ): UpdateCartItemResult {
        //TODO 수정
        val latch = CountDownLatch(1)
        var exception: Exception? = null
        return UpdateCartItemResult.UPDATED(CartItemResult(0, CartItemCounter()))
    }

    override fun getTotalCartItemCount(): Int {
        val latch = CountDownLatch(1)
        var cartItemCount: Int = ERROR_QUANTITY_SIZE
        var exception: Exception? = null

        cartItemDataSource.loadCartItemCount()
            .enqueue(object : Callback<CartItemQuantityDto>{
                override fun onResponse(
                    p0: Call<CartItemQuantityDto>,
                    response: Response<CartItemQuantityDto>
                ) {
                    cartItemCount = response.body()?.quantity ?: ERROR_QUANTITY_SIZE
                    latch.countDown()
                }

                override fun onFailure(p0: Call<CartItemQuantityDto>, t: Throwable) {
                    exception = Exception(t.message)
                    latch.countDown()
                }
            })
        latch.awaitOrThrow(exception)
        if (cartItemCount  == ERROR_QUANTITY_SIZE) throw NoSuchDataException()
        return cartItemCount
    }

    companion object{
        private const val ERROR_QUANTITY_SIZE = -1
        private const val MAX_CART_ITEM_SIZE = 50
    }
}
