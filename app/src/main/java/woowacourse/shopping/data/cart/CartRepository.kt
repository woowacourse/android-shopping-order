package woowacourse.shopping.data.cart

import woowacourse.shopping.data.dto.request.RequestCartItemPostDto
import woowacourse.shopping.data.dto.request.RequestCartItemsPatchDto
import woowacourse.shopping.data.dto.response.ResponseCartItemCountsGetDto
import woowacourse.shopping.data.dto.response.ResponseCartItemGetDto
import woowacourse.shopping.data.service.ApiFactory
import woowacourse.shopping.model.Quantity
import kotlin.concurrent.thread

interface CartRepository {
    fun insert(cart: Cart): Long

    fun find(id: Long): Cart

    fun findAll(): List<Cart>

    fun delete(id: Long)

    fun deleteByProductId(productId: Long)

    fun deleteAll()

    fun itemSize(): Int

    fun getProducts(
        page: Int,
        pageSize: Int,
    ): List<Cart>

    fun plusQuantityByProductId(productId: Long)

    fun minusQuantityByProductId(productId: Long)
}


interface CartRepositoryTest {

    fun getCartItems(page: Int, size: Int): List<Cart>


    fun postCartItems(
        productId: Long, quantity: Int
    )

    fun deleteCartItem(
        id: Long
    )

    fun getCartItemCounts(): Int

    fun patchCartItem(id:Long, quantity: Int)
}

object CartRepositoryTestImpl : CartRepositoryTest {
    override fun getCartItems(page: Int, size: Int): List<Cart> {
        var cartsDto: ResponseCartItemGetDto? = null
        thread {
            cartsDto = ApiFactory.getCartItems(page, size)

        }.join()
        val carts = cartsDto ?: error("장바구니 정보를 불러올 수 없습니다.")
        return carts.content.map {
            Cart(id = it.id, productId = it.product.id, quantity = Quantity(it.quantity))
        }
    }

    override fun postCartItems(productId: Long, quantity: Int) {
        thread {
            ApiFactory.postCartItems(
                RequestCartItemPostDto(
                    productId = productId,
                    quantity = quantity
                )
            )
        }.join()
    }

    override fun deleteCartItem(id: Long) {
        thread {
            ApiFactory.deleteCartItems(id)
        }.join()
    }

    override fun getCartItemCounts(): Int{
        var cartCountDto: ResponseCartItemCountsGetDto? = null
        thread {
            cartCountDto = ApiFactory.getCartItemCounts()
        }.join()
        val count = cartCountDto ?: error("장바구니 아이템 수량을 조회할 수 없습니다.")
        return count.quantity
    }

    override fun patchCartItem(id: Long, quantity:Int) {
        thread {
            ApiFactory.patchCartItems(id = id, request = RequestCartItemsPatchDto(quantity))
        }.join()
    }

}
