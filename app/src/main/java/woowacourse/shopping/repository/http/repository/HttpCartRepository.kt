package woowacourse.shopping.repository.http.repository

import kotlinx.serialization.SerializationException
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
import woowacourse.shopping.repository.http.api.CartApiService
import woowacourse.shopping.repository.http.dto.cart.CartItemQuantityUpdateRequestDto
import woowacourse.shopping.repository.http.dto.cart.CartItemRequestDto
import woowacourse.shopping.repository.http.dto.cart.CartItemResponseDto
import woowacourse.shopping.repository.http.dto.cart.CartPageResponseDto
import woowacourse.shopping.repository.http.dto.cart.OrderRequestDto
import woowacourse.shopping.repository.http.exception.CartNetworkException
import woowacourse.shopping.repository.http.exception.CartParsingException
import woowacourse.shopping.repository.http.exception.CartRemoteException
import woowacourse.shopping.repository.http.exception.CartResponseException
import woowacourse.shopping.repository.query.CartPageItem
import woowacourse.shopping.repository.query.CartPageResult
import java.io.IOException

private const val NETWORK_PAGE_SIZE = 100
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

    override suspend fun createOrder(cartItemIds: List<Long>): Result<Unit> {
        if (cartItemIds.isEmpty()) return Result.success(Unit)

        val result =
            execute("주문 API 호출에 실패했습니다.") {
                cartApiService.createOrder(
                    OrderRequestDto(cartItemIds = cartItemIds),
                )
            }

        if (result.isFailure) {
            return Result.failure(result.exceptionOrNull()!!)
        }

        return Result.success(Unit)
    }

    override suspend fun getCartPage(
        page: Int,
        size: Int,
    ): Result<CartPageResult> {
        val result =
            execute("장바구니 조회 API 호출에 실패했습니다.") {
                cartApiService.getCartItems(page = page, size = size)
            }

        if (result.isFailure) {
            return Result.failure(result.exceptionOrNull()!!)
        }

        val body = result.getOrNull()!!

        return Result.success(
            CartPageResult(
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
            ),
        )
    }

    override suspend fun setQuantity(
        productId: Long,
        quantity: Int,
    ): Result<Unit> {
        if (quantity < 0) {
            return Result.failure(
                IllegalArgumentException("수량은 0 이상이어야 합니다."),
            )
        }

        val existingCartItemResult = findCartItemByProductId(productId)

        if (existingCartItemResult.isFailure) {
            return Result.failure(existingCartItemResult.exceptionOrNull()!!)
        }

        val existingCartItem = existingCartItemResult.getOrNull()

        return when {
            existingCartItem == null && quantity == 0 -> {
                Result.success(Unit)
            }

            existingCartItem == null -> {
                val result =
                    execute("장바구니 추가 API 호출에 실패했습니다.") {
                        cartApiService.addCartItem(
                            CartItemRequestDto(
                                productId = productId,
                                quantity = quantity,
                            ),
                        )
                    }

                result.toUnitResult()
            }

            quantity == 0 -> {
                val result =
                    execute("장바구니 삭제 API 호출에 실패했습니다.") {
                        cartApiService.deleteCartItem(existingCartItem.id)
                    }

                result.toUnitResult()
            }

            else -> {
                val result =
                    execute("장바구니 수량 변경 API 호출에 실패했습니다.") {
                        cartApiService.updateCartItemQuantity(
                            id = existingCartItem.id,
                            body = CartItemQuantityUpdateRequestDto(quantity = quantity),
                        )
                    }

                result.toUnitResult()
            }
        }
    }

    override suspend fun getCartItemsByProductIds(productIds: Set<Long>): Result<List<CartItem>> {
        if (productIds.isEmpty()) return Result.success(emptyList())

        val allItemsResult = fetchAllCartItems()

        if (allItemsResult.isFailure) {
            return Result.failure(allItemsResult.exceptionOrNull()!!)
        }

        val allItems = allItemsResult.getOrNull()!!

        val cartItems =
            allItems
                .filter { it.product.id in productIds }
                .map {
                    CartItem(
                        productId = (it.product.id),
                        quantity = it.quantity,
                    )
                }

        return Result.success(cartItems)
    }

    override suspend fun count(): Result<Int> {
        val result = fetchCartItemPage(size = 1)

        if (result.isFailure) {
            return Result.failure(result.exceptionOrNull()!!)
        }

        return Result.success(result.getOrNull()!!.totalElements.toInt())
    }

    private suspend fun findCartItemByProductId(productId: Long): Result<CartItemResponseDto?> {
        var page = 0

        while (true) {
            val responseResult = fetchCartItemPage(page = page, size = NETWORK_PAGE_SIZE)

            if (responseResult.isFailure) {
                return Result.failure(responseResult.exceptionOrNull()!!)
            }

            val response = responseResult.getOrNull()!!

            response.content
                .firstOrNull { it.product.id == productId }
                ?.let { return Result.success(it) }

            if (page + 1 >= response.totalPages) {
                return Result.success(null)
            }

            page += 1
        }
    }

    private suspend fun fetchCartItemPage(
        page: Int = 0,
        size: Int,
    ): Result<CartPageResponseDto> =
        execute("장바구니 조회 API 호출에 실패했습니다.") {
            cartApiService.getCartItems(page = page, size = size)
        }

    private suspend fun fetchAllCartItems(): Result<List<CartItemResponseDto>> {
        val allCartItems = mutableListOf<CartItemResponseDto>()

        var currentPage = 0

        while (true) {
            val responseResult =
                execute("장바구니 조회 API 호출에 실패했습니다.") {
                    cartApiService.getCartItems(page = currentPage, size = NETWORK_PAGE_SIZE)
                }

            if (responseResult.isFailure) {
                return Result.failure(responseResult.exceptionOrNull()!!)
            }

            val response = responseResult.getOrNull()!!

            allCartItems.addAll(response.content)

            if (currentPage + 1 >= response.totalPages) {
                break
            }

            currentPage += 1
        }

        return Result.success(allCartItems)
    }

    private suspend fun <T> execute(
        errorMessage: String,
        request: suspend () -> Response<T>,
    ): Result<T> =
        try {
            val response = request()

            if (!response.isSuccessful) {
                Result.failure(
                    CartResponseException(
                        code = response.code(),
                        message = "$errorMessage code=${response.code()}",
                    ),
                )
            } else {
                val body = response.body()

                if (body == null) {
                    Result.failure(
                        CartParsingException(
                            message = "장바구니 API 응답 본문이 비어 있습니다.",
                            cause = IllegalStateException("response body is null"),
                        ),
                    )
                } else {
                    Result.success(body)
                }
            }
        } catch (exception: CartRemoteException) {
            Result.failure(exception)
        } catch (exception: IOException) {
            Result.failure(
                CartNetworkException(
                    message = errorMessage,
                    cause = exception,
                ),
            )
        } catch (exception: SerializationException) {
            Result.failure(
                CartParsingException(
                    message = "장바구니 API 응답이 올바르지 않습니다.",
                    cause = exception,
                ),
            )
        }

    private fun <T> Result<T>.toUnitResult(): Result<Unit> =
        if (isSuccess) {
            Result.success(Unit)
        } else {
            Result.failure(exceptionOrNull()!!)
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
