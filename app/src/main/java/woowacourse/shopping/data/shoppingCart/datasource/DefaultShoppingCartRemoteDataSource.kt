package woowacourse.shopping.data.shoppingCart.datasource

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.shoppingCart.remote.dto.CartCountsResponseDto
import woowacourse.shopping.data.shoppingCart.remote.dto.CartItemQuantityRequestDto
import woowacourse.shopping.data.shoppingCart.remote.dto.CartItemRequestDto
import woowacourse.shopping.data.shoppingCart.remote.dto.ShoppingCartItemsResponseDto
import woowacourse.shopping.data.shoppingCart.remote.service.ShoppingCartService

class DefaultShoppingCartRemoteDataSource(
    private val shoppingCartService: ShoppingCartService,
) : ShoppingCartRemoteDataSource {
    override fun getCartCounts(onCallback: (Result<CartCountsResponseDto?>) -> Unit) {
        shoppingCartService.getCartCounts().enqueue(
            object : Callback<CartCountsResponseDto> {
                override fun onResponse(
                    call: Call<CartCountsResponseDto?>,
                    response: Response<CartCountsResponseDto?>,
                ) {
                    onCallback(Result.success(response.body()))
                }

                override fun onFailure(
                    call: Call<CartCountsResponseDto?>,
                    t: Throwable,
                ) {
                    onCallback(Result.failure(t))
                }
            },
        )
    }

    override fun saveCartItem(
        cartItemRequestDto: CartItemRequestDto,
        onCallback: (Result<Unit>) -> Unit,
    ) {
        shoppingCartService.postCartItem(cartItemRequestDto).enqueue(
            object : Callback<Unit> {
                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>,
                ) {
                    onCallback(Result.success(Unit))
                }

                override fun onFailure(
                    call: Call<Unit>,
                    t: Throwable,
                ) {
                    onCallback(Result.failure(t))
                }
            },
        )
    }

    override fun updateCartItemQuantity(
        shoppingCartId: Long,
        cartItemQuantityRequestDto: CartItemQuantityRequestDto,
        onCallback: (Result<Unit>) -> Unit,
    ) {
        shoppingCartService
            .patchCartItem(
                shoppingCartId = shoppingCartId,
                cartItemQuantityRequestDto = cartItemQuantityRequestDto,
            ).enqueue(
                object : Callback<Unit> {
                    override fun onResponse(
                        call: Call<Unit>,
                        response: Response<Unit>,
                    ) {
                        onCallback(Result.success(Unit))
                    }

                    override fun onFailure(
                        call: Call<Unit>,
                        t: Throwable,
                    ) {
                        onCallback(Result.failure(t))
                    }
                },
            )
    }

    override fun getCartItems(
        page: Int,
        size: Int,
        onCallback: (Result<ShoppingCartItemsResponseDto?>) -> Unit,
    ) {
        shoppingCartService.getCartItems(page, size).enqueue(
            object : Callback<ShoppingCartItemsResponseDto> {
                override fun onResponse(
                    call: Call<ShoppingCartItemsResponseDto>,
                    response: Response<ShoppingCartItemsResponseDto>,
                ) {
                    onCallback(Result.success(response.body()))
                }

                override fun onFailure(
                    call: Call<ShoppingCartItemsResponseDto>,
                    t: Throwable,
                ) {
                    onCallback(Result.failure(t))
                }
            },
        )
    }

    override fun deleteCartItem(
        shoppingCartId: Long,
        onCallback: (Result<Unit>) -> Unit,
    ) {
        shoppingCartService.deleteCartItem(shoppingCartId).enqueue(
            object : Callback<Unit> {
                override fun onResponse(
                    call: Call<Unit?>,
                    response: Response<Unit?>,
                ) {
                    if (response.isSuccessful) {
                        onCallback(Result.success(Unit))
                    }
                }

                override fun onFailure(
                    call: Call<Unit?>,
                    t: Throwable,
                ) {
                    onCallback(Result.failure(t))
                }
            },
        )
    }
}
