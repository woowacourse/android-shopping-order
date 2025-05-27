package woowacourse.shopping.data.shoppingCart.repository

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.shoppingCart.local.dao.ShoppingCartDao
import woowacourse.shopping.data.shoppingCart.remote.dto.CartCountsResponseDto
import woowacourse.shopping.data.shoppingCart.remote.dto.CartItemQuantityRequestDto
import woowacourse.shopping.data.shoppingCart.remote.dto.CartItemRequestDto
import woowacourse.shopping.data.shoppingCart.remote.dto.ShoppingCartItemsResponseDto
import woowacourse.shopping.data.shoppingCart.remote.service.ShoppingCartService
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.domain.shoppingCart.ShoppingCartProduct
import kotlin.concurrent.thread

class DefaultShoppingCartRepository(
    private val shoppingCartDao: ShoppingCartDao,
    private val shoppingCartService: ShoppingCartService,
) : ShoppingCartRepository {
    override fun load(
        page: Int,
        size: Int,
        onResult: (Result<List<ShoppingCartProduct>>) -> Unit,
    ) {
        shoppingCartService
            .getCartItems(page, size)
            .enqueue(
                object : Callback<ShoppingCartItemsResponseDto> {
                    override fun onResponse(
                        call: Call<ShoppingCartItemsResponseDto>,
                        response: Response<ShoppingCartItemsResponseDto>,
                    ) {
                        onResult(Result.success(response.body()?.toDomain() ?: emptyList()))
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

    override fun decreaseQuantity(
        product: Product,
        quantity: Int,
        onResult: (Result<Unit>) -> Unit,
    ) {
        val requestDto = CartItemQuantityRequestDto(quantity = quantity)
        shoppingCartService
            .updateCartItemQuantity(
                productId = product.id,
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
        product: Product,
        onResult: (Result<Unit>) -> Unit,
    ) {
        thread {
            runCatching {
                shoppingCartDao.delete(product.id)
            }.onSuccess {
                onResult(Result.success(Unit))
            }.onFailure { exception ->
                onResult(Result.failure(exception))
            }
        }
    }

    override fun fetchSelectedQuantity(
        product: Product,
        onResult: (Result<Int?>) -> Unit,
    ) {
        thread {
            runCatching {
                shoppingCartDao.getQuantity(product.id)
            }.onSuccess { quantity: Int? ->
                onResult(Result.success(quantity))
            }.onFailure { exception ->
                onResult(Result.failure(exception))
            }
        }
    }

    override fun fetchSelectedQuantity(
        products: List<Product>,
        onResult: (Result<List<ShoppingCartProduct>>) -> Unit,
    ) {
        thread {
            runCatching {
                products.map { product ->
                    val quantity = shoppingCartDao.getQuantity(product.id)
                    ShoppingCartProduct(
                        product = product,
                        quantity = quantity,
                    )
                }
            }.onSuccess { shoppingCartProducts ->
                onResult(Result.success(shoppingCartProducts))
            }.onFailure { exception ->
                onResult(Result.failure(exception))
            }
        }
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
        private var INSTANCE: ShoppingCartRepository? = null

        fun initialize(
            shoppingCartDao: ShoppingCartDao,
            shoppingCartService: ShoppingCartService,
        ) {
            if (INSTANCE == null) {
                INSTANCE =
                    DefaultShoppingCartRepository(
                        shoppingCartDao = shoppingCartDao,
                        shoppingCartService = shoppingCartService,
                    )
            }
        }

        fun get(): ShoppingCartRepository = INSTANCE ?: throw IllegalStateException("초기화 되지 않았습니다.")
    }
}
