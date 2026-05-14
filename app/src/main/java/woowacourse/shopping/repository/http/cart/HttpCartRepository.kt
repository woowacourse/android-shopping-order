package woowacourse.shopping.repository.http.cart

import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import woowacourse.shopping.model.CartItem
import woowacourse.shopping.repository.CartRepository
import woowacourse.shopping.repository.query.CartPageItem
import woowacourse.shopping.repository.query.CartPageResult

private val NETWORK_JSON =
    Json {
        ignoreUnknownKeys = true
    }

class HttpCartRepository(
    private val cartApiService: CartApiService,
) : CartRepository {
    constructor(
        client: OkHttpClient,
        baseUrlProvider: () -> HttpUrl,
    ) : this(
        cartApiService = createCartApiService(client, baseUrlProvider),
    )

    constructor(
        client: OkHttpClient,
        baseUrl: String,
    ) : this(
        client = client,
        baseUrlProvider = {
            requireNotNull(baseUrl.toHttpUrlOrNull()) { "유효한 cart API baseUrl이 필요합니다." }
        },
    )

    override suspend fun createOrder(cartItemIds: List<Long>) {
        if (cartItemIds.isEmpty()) return

        execute("주문 API 호출에 실패했습니다.") {
            cartApiService.createOrder(
                OrderRequestDto(cartItemIds = cartItemIds),
            )
        }
    }

    override suspend fun getCartPage(
        page: Int,
        size: Int,
    ): CartPageResult {
        val body =
            execute("장바구니 조회 API 호출에 실패했습니다.") {
                cartApiService.getCartItems(page = page, size = size)
            }

        return CartPageResult(
            items =
                body.content.map { item ->
                    CartPageItem(
                        cartItemId = item.id,
                        productId = (item.product.id),
                        quantity = item.quantity,
                    )
                },
            totalElements = body.totalElements.toInt(),
            totalPages = body.totalPages,
            page = body.number,
        )
    }

    override suspend fun setQuantity(
        productId: Long,
        quantity: Int,
    ) {
        require(quantity >= 0) { "수량은 0 이상이어야 합니다." }

        val existingCartItem = findCartItemByProductId(productId)

        when {
            existingCartItem == null && quantity == 0 -> return

            existingCartItem == null -> {
                execute("장바구니 추가 API 호출에 실패했습니다.") {
                    cartApiService.addCartItem(
                        CartItemRequestDto(
                            productId = productId,
                            quantity = quantity,
                        ),
                    )
                }
            }

            quantity == 0 -> {
                execute("장바구니 삭제 API 호출에 실패했습니다.") {
                    cartApiService.deleteCartItem(existingCartItem.id)
                }
            }

            else -> {
                execute("장바구니 수량 변경 API 호출에 실패했습니다.") {
                    cartApiService.updateCartItemQuantity(
                        id = existingCartItem.id,
                        body = CartItemQuantityUpdateRequestDto(quantity = quantity),
                    )
                }
            }
        }
    }

    override suspend fun getCartItemsByProductIds(productIds: Set<Long>): List<CartItem> {
        if (productIds.isEmpty()) return emptyList()

        val firstPage = fetchCartItemPage(size = 100)

        return firstPage.content
            .filter { (it.product.id) in productIds }
            .map {
                CartItem(
                    productId = (it.product.id),
                    quantity = it.quantity,
                )
            }
    }

    override suspend fun count(): Int = fetchCartItemPage(size = 1).totalElements.toInt()

    private suspend fun findCartItemByProductId(productId: Long): CartItemResponseDto? {
        var page = 0

        while (true) {
            val response = fetchCartItemPage(page = page, size = 100)

            response.content.firstOrNull { it.product.id == productId }?.let { return it }

            if (page + 1 >= response.totalPages) {
                return null
            }
            page += 1
        }
    }

    private suspend fun fetchCartItemPage(
        page: Int = 0,
        size: Int,
    ) = execute("장바구니 조회 API 호출에 실패했습니다.") {
        cartApiService.getCartItems(page = page, size = size)
    }

    private suspend fun <T> execute(
        errorMessage: String,
        request: suspend () -> Response<T>,
    ): T {
        val response = request()
        if (!response.isSuccessful) {
            error("$errorMessage code=${response.code()}")
        }
        @Suppress("UNCHECKED_CAST")
        return response.body() ?: Unit as T
    }

    companion object {
        private fun createCartApiService(
            client: OkHttpClient,
            baseUrlProvider: () -> HttpUrl,
        ): CartApiService =
            Retrofit
                .Builder()
                .baseUrl(baseUrlProvider())
                .client(client)
                .addConverterFactory(NETWORK_JSON.asConverterFactory("application/json".toMediaType()))
                .build()
                .create(CartApiService::class.java)
    }
}
