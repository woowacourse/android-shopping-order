package woowacourse.shopping.data.datasource.remote

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.dto.cart.CartsResponse
import woowacourse.shopping.data.dto.cart.toDomain
import woowacourse.shopping.data.remote.CartItemService
import woowacourse.shopping.domain.model.CartItem

class CartDataSourceImpl(
    private val carItemService: CartItemService,
) : CartDataSource {
//    override fun getCartProductCount(): Int = carItemService.getAllProductCount()
//
//    override fun getTotalQuantity(): Int? = carItemService.getTotalQuantity()
//
//    override fun getQuantityById(productId: Long): Int = carItemService.getQuantityById(productId)

    override fun getPagedCartProducts(
        page: Int,
        size: Int,
        onResult: (List<CartItem>) -> Unit,
    ) = carItemService.requestCartItems(page = page, size = size).enqueue(
        object : Callback<CartsResponse> {
            override fun onResponse(
                call: Call<CartsResponse>,
                response: Response<CartsResponse>,
            ) {
                if (response.isSuccessful) {
                    val body =
                        response.body()?.cartContent?.map { it.cartProduct.toDomain(it.quantity) }
                            ?: emptyList()
                    onResult(body)
                }
            }

            override fun onFailure(
                call: Call<CartsResponse>,
                t: Throwable,
            ) {
                onResult(emptyList())
            }
        },
    )
//
//    override fun existsByProductId(productId: Long): Boolean = carItemService.existsByProductId(productId)
//
//    override fun increaseQuantity(
//        productId: Long,
//        quantity: Int,
//    ) = carItemService.increaseQuantity(productId, quantity)
//
//    override fun decreaseQuantity(productId: Long) = carItemService.decreaseQuantity(productId)
//
//    override fun insertProduct(cartEntity: CartEntity) = carItemService.insertProduct(cartEntity)
//
//    override fun deleteProductById(productId: Long) = carItemService.deleteProductById(productId)
}
