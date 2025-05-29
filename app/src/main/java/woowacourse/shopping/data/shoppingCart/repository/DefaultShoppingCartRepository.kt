package woowacourse.shopping.data.shoppingCart.repository

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.shoppingCart.remote.dto.CartCountsResponseDto
import woowacourse.shopping.data.shoppingCart.remote.dto.CartItemQuantityRequestDto
import woowacourse.shopping.data.shoppingCart.remote.dto.CartItemRequestDto
import woowacourse.shopping.data.shoppingCart.remote.dto.ShoppingCartItemsResponseDto
import woowacourse.shopping.data.shoppingCart.remote.service.ShoppingCartService
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.domain.shoppingCart.ShoppingCarts

class DefaultShoppingCartRepository(
    private val shoppingCartService: ShoppingCartService,
) : ShoppingCartRepository {
    override fun load(
        page: Int,
        size: Int,
        onResult: (Result<ShoppingCarts>) -> Unit,
    ) {
        shoppingCartService
            .getCartItems(page, size)
            .enqueue(
                object : Callback<ShoppingCartItemsResponseDto> {
                    override fun onResponse(
                        call: Call<ShoppingCartItemsResponseDto>,
                        response: Response<ShoppingCartItemsResponseDto>,
                    ) {
                        onResult(
                            Result.success(
                                response.body()?.toDomain() ?: ShoppingCarts(false, emptyList()),
                            ),
                        )
                    }

                    override fun onFailure(
                        call: Call<ShoppingCartItemsResponseDto>,
                        t: Throwable,
                    ) {
                        onResult(Result.failure(t))
                    }
                },
            )
    }

    override fun add(
        product: Product,
        quantity: Int,
        onResult: (Result<Unit>) -> Unit,
    ) {
        shoppingCartService
            .postCartItem(
                CartItemRequestDto(
                    productId = product.id,
                    quantity = quantity,
                ),
            ).enqueue(
                object : Callback<Unit> {
                    override fun onResponse(
                        call: Call<Unit>,
                        response: Response<Unit>,
                    ) {
                        onResult(Result.success(Unit))
                    }

                    override fun onFailure(
                        call: Call<Unit>,
                        t: Throwable,
                    ) {
                        onResult(Result.failure(t))
                    }
                },
            )
    }

    override fun increaseQuantity(
        shoppingCartId: Long,
        quantity: Int,
        onResult: (Result<Unit>) -> Unit,
    ) {
        updateShoppingCartQuantity(quantity, shoppingCartId, onResult)
    }

    override fun decreaseQuantity(
        shoppingCartId: Long,
        quantity: Int,
        onResult: (Result<Unit>) -> Unit,
    ) {
        updateShoppingCartQuantity(quantity, shoppingCartId, onResult)
    }

    private fun updateShoppingCartQuantity(
        quantity: Int,
        shoppingCartId: Long,
        onResult: (Result<Unit>) -> Unit,
    ) {
        val requestDto = CartItemQuantityRequestDto(quantity = quantity)
        shoppingCartService
            .updateCartItemQuantity(
                shoppingCartId = shoppingCartId,
                cartItemQuantityRequestDto = requestDto,
            ).enqueue(
                object : Callback<Unit> {
                    override fun onResponse(
                        call: Call<Unit>,
                        response: Response<Unit>,
                    ) {
                        onResult(Result.success(Unit))
                    }

                    override fun onFailure(
                        call: Call<Unit>,
                        t: Throwable,
                    ) {
                        onResult(Result.failure(t))
                    }
                },
            )
    }

    override fun remove(
        shoppingCartId: Long,
        onResult: (Result<Unit>) -> Unit,
    ) {
        shoppingCartService.deleteCartItem(shoppingCartId).enqueue(
            object : Callback<Unit> {
                override fun onResponse(
                    call: Call<Unit?>,
                    response: Response<Unit?>,
                ) {
                    if (response.isSuccessful) {
                        onResult(Result.success(Unit))
                    }
                }

                override fun onFailure(
                    call: Call<Unit?>,
                    t: Throwable,
                ) {
                    onResult(Result.failure(t))
                }
            },
        )
    }

    override fun fetchAllQuantity(onResult: (Result<Int>) -> Unit) {
        shoppingCartService.getCartCounts().enqueue(
            object : Callback<CartCountsResponseDto> {
                override fun onResponse(
                    call: Call<CartCountsResponseDto?>,
                    response: Response<CartCountsResponseDto?>,
                ) {
                    onResult(Result.success(response.body()?.quantity ?: 0))
                }

                override fun onFailure(
                    call: Call<CartCountsResponseDto?>,
                    t: Throwable,
                ) {
                    onResult(Result.failure(t))
                }
            },
        )
    }

    companion object {
        @Suppress("ktlint:standard:property-naming")
        private var INSTANCE: ShoppingCartRepository? = null

        fun initialize(shoppingCartService: ShoppingCartService) {
            if (INSTANCE == null) {
                INSTANCE =
                    DefaultShoppingCartRepository(
                        shoppingCartService = shoppingCartService,
                    )
            }
        }

        fun get(): ShoppingCartRepository = INSTANCE ?: throw IllegalStateException("초기화 되지 않았습니다.")
    }
}
