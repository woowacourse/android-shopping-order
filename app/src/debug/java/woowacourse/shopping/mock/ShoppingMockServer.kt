package woowacourse.shopping.mock

import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import woowacourse.shopping.repository.http.dto.cart.CartItemCountResponseDto
import woowacourse.shopping.repository.http.dto.cart.CartItemQuantityUpdateRequestDto
import woowacourse.shopping.repository.http.dto.cart.CartItemRequestDto
import woowacourse.shopping.repository.http.dto.cart.CartItemResponseDto
import woowacourse.shopping.repository.http.dto.cart.CartPageResponseDto
import woowacourse.shopping.repository.http.dto.cart.OrderRequestDto
import woowacourse.shopping.repository.http.dto.product.ProductPageResponseDto
import woowacourse.shopping.repository.http.dto.product.ProductResponseDto
import java.net.HttpURLConnection
import java.net.InetAddress

object ShoppingMockServer {
    private const val PORT = 8080
    private val server =
        MockWebServer().apply {
            dispatcher = ShoppingMockDispatcher()
        }

    fun start() {
        if (serverStarted) return
        server.start(loopbackAddress(), PORT)
        serverStarted = true
    }

    @Volatile
    private var serverStarted: Boolean = false

    private fun loopbackAddress(): InetAddress = InetAddress.getByAddress(byteArrayOf(127, 0, 0, 1))
}

private val json = Json { ignoreUnknownKeys = true }

private class ShoppingMockDispatcher : Dispatcher() {
    private val repository = MockShoppingRepository()

    override fun dispatch(request: RecordedRequest): MockResponse {
        val url = request.requestUrl ?: return errorResponse(HttpURLConnection.HTTP_BAD_REQUEST)
        return when {
            request.method == "GET" && url.encodedPath == "/products" -> products(url)
            request.method == "GET" && url.pathSegments.size == 2 && url.pathSegments[0] == "products" -> productDetail(url)
            request.method == "GET" && url.encodedPath == "/cart-items" -> cartItems(url)
            request.method == "GET" && url.encodedPath == "/cart-items/counts" -> count()
            request.method == "POST" && url.encodedPath == "/cart-items" -> addCartItem(request)
            request.method == "PATCH" && url.pathSegments.size == 2 && url.pathSegments[0] == "cart-items" -> updateCartItem(url, request)
            request.method == "DELETE" && url.pathSegments.size == 2 && url.pathSegments[0] == "cart-items" -> deleteCartItem(url)
            request.method == "POST" && url.encodedPath == "/orders" -> order(request)
            else -> errorResponse(HttpURLConnection.HTTP_NOT_FOUND)
        }
    }

    private fun products(url: HttpUrl): MockResponse {
        val page = url.queryParameter("page")?.toIntOrNull() ?: 0
        val size = url.queryParameter("size")?.toIntOrNull() ?: 20
        val category = url.queryParameter("category")
        return jsonResponse(repository.getProducts(page, size, category))
    }

    private fun productDetail(url: HttpUrl): MockResponse {
        val id = url.pathSegments[1].toLongOrNull() ?: return errorResponse(HttpURLConnection.HTTP_BAD_REQUEST)
        val product = repository.getProduct(id) ?: return errorResponse(HttpURLConnection.HTTP_NOT_FOUND)
        return jsonResponse(product)
    }

    private fun cartItems(url: HttpUrl): MockResponse {
        val page = url.queryParameter("page")?.toIntOrNull() ?: 0
        val size = url.queryParameter("size")?.toIntOrNull() ?: 20
        return jsonResponse(repository.getCartItems(page, size))
    }

    private fun count(): MockResponse = jsonResponse(CartItemCountResponseDto(repository.getCartItemsCount()))

    private fun addCartItem(request: RecordedRequest): MockResponse {
        val payload = json.decodeFromString<CartItemRequestDto>(request.body.readUtf8())
        repository.addCartItem(payload.productId, payload.quantity)
        return emptyResponse(HttpURLConnection.HTTP_CREATED)
    }

    private fun updateCartItem(
        url: HttpUrl,
        request: RecordedRequest,
    ): MockResponse {
        val id = url.pathSegments[1].toLongOrNull() ?: return errorResponse(HttpURLConnection.HTTP_BAD_REQUEST)
        val payload = json.decodeFromString<CartItemQuantityUpdateRequestDto>(request.body.readUtf8())
        repository.updateCartItem(id, payload.quantity)
        return emptyResponse(HttpURLConnection.HTTP_OK)
    }

    private fun deleteCartItem(url: HttpUrl): MockResponse {
        val id = url.pathSegments[1].toLongOrNull() ?: return errorResponse(HttpURLConnection.HTTP_BAD_REQUEST)
        repository.deleteCartItem(id)
        return emptyResponse(HttpURLConnection.HTTP_NO_CONTENT)
    }

    private fun order(request: RecordedRequest): MockResponse {
        val payload = json.decodeFromString<OrderRequestDto>(request.body.readUtf8())
        repository.order(payload.cartItemIds)
        return emptyResponse(HttpURLConnection.HTTP_OK)
    }

    private fun jsonResponse(body: Any): MockResponse =
        MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setHeader("Content-Type", "application/json")
            .setBody(
                when (body) {
                    is ProductPageResponseDto -> json.encodeToString(body)
                    is ProductResponseDto -> json.encodeToString(body)
                    is CartPageResponseDto -> json.encodeToString(body)
                    is CartItemCountResponseDto -> json.encodeToString(body)
                    else -> error("Unsupported response type: ${body::class.java.simpleName}")
                },
            )

    private fun emptyResponse(code: Int): MockResponse = MockResponse().setResponseCode(code)

    private fun errorResponse(code: Int): MockResponse =
        MockResponse()
            .setResponseCode(code)
            .setHeader("Content-Type", "application/json")
            .setBody("""{"code":$code}""")
}

private class MockShoppingRepository {
    private val products =
        listOf(
            // ===== 시즌 음료 / 주스 / 스무디 =====
            product(1L, "꿀수박주스", 4500, "SEASON"),
            product(2L, "수박소르베 밀키 스무디", 5500, "SMOOTHIE"),
            product(3L, "수박 리치코코 슬러시", 5300, "SMOOTHIE"),
            product(4L, "파인망고코코 스무디", 5300, "SMOOTHIE"),
            product(5L, "자몽 톡톡 스무디", 4900, "SMOOTHIE"),
            product(6L, "제로 레몬말차 아이스티", 3900, "TEA"),
            product(7L, "저당 꿀배 XO야쿠르트", 4500, "BEVERAGE"),
            // ===== 왕메가 / 디카페인 =====
            product(8L, "귤 톡톡 젤리스무디", 4900, "SMOOTHIE"),
            product(9L, "왕메가카페라떼", 4500, "COFFEE"),
            product(10L, "디카페인 왕메가카페라떼", 4800, "DECAFFEINE"),
            product(11L, "디카페인 라이트 바닐라 아몬드라떼", 4500, "DECAFFEINE"),
            product(12L, "왕메가사과유자", 4500, "TEA"),
            product(13L, "왕메가헛개리카노", 3900, "COFFEE"),
            product(14L, "(HOT)디카페인 헛개리카노", 2800, "DECAFFEINE"),
            // ===== 주스 / 디카페인 =====
            product(15L, "코코넛 커피 스무디", 5300, "SMOOTHIE"),
            product(16L, "딸기주스", 4500, "JUICE"),
            product(17L, "딸기바나나주스", 4500, "JUICE"),
            product(18L, "디카페인 에스프레소", 2500, "DECAFFEINE"),
            // ===== 디카페인 음료 =====
            product(19L, "디카페인 카페라떼", 3500, "DECAFFEINE"),
            product(20L, "디카페인 카푸치노", 3500, "DECAFFEINE"),
            product(21L, "디카페인 바닐라라떼", 4200, "DECAFFEINE"),
            product(22L, "디카페인 헤이즐넛 라떼", 4200, "DECAFFEINE"),
            product(23L, "디카페인 카라멜마끼아또", 4500, "DECAFFEINE"),
            // ===== 시즌 라떼 / 핫 음료 =====
            product(24L, "오레오초코라떼", 4500, "LATTE"),
            product(25L, "토피넛라떼", 4500, "LATTE"),
            product(26L, "흑당버블밀크티라떼", 4500, "LATTE"),
            product(27L, "핫초코", 3500, "BEVERAGE"),
            product(28L, "녹차라떼", 3900, "LATTE"),
            product(29L, "로얄밀크티라떼", 3900, "LATTE"),
            product(30L, "흑당라떼", 4500, "LATTE"),
            product(31L, "흑당밀크티라떼", 4500, "LATTE"),
            product(32L, "흑당버블라떼", 4500, "LATTE"),
            // ===== 에스프레소 클래식 =====
            product(33L, "카페모카", 3500, "COFFEE"),
            product(34L, "카푸치노", 3500, "COFFEE"),
            product(35L, "콜드브루라떼", 3500, "COFFEE"),
            product(36L, "콜드브루오리지널", 3500, "COFFEE"),
            product(37L, "헤이즐넛라떼", 3500, "COFFEE"),
            product(38L, "헤이즐넛아메리카노", 2500, "COFFEE"),
            product(39L, "꿀아메리카노", 3500, "COFFEE"),
            product(40L, "바닐라라떼", 3500, "COFFEE"),
            // ===== 스무디 / 프라페 =====
            product(41L, "딸기요거트스무디", 5300, "SMOOTHIE"),
            product(42L, "딸기퐁크러쉬", 5500, "FRAPPE"),
            product(43L, "리얼초코프라페", 4500, "FRAPPE"),
            product(44L, "망고요거트스무디", 5300, "SMOOTHIE"),
            product(45L, "민트프라페", 4500, "FRAPPE"),
            product(46L, "바나나퐁크러쉬", 5500, "FRAPPE"),
            product(47L, "초코허니퐁크러쉬", 5500, "FRAPPE"),
            product(48L, "커피프라페", 4500, "FRAPPE"),
            // ===== 티 =====
            product(49L, "캐모마일 (HOT)", 3500, "TEA"),
            product(50L, "페퍼민트 (HOT)", 3500, "TEA"),
            product(51L, "녹차 (ICE)", 3500, "TEA"),
            product(52L, "사과유자차", 3900, "TEA"),
            product(53L, "얼그레이", 3500, "TEA"),
            product(54L, "캐모마일 (ICE)", 3500, "TEA"),
            product(55L, "페퍼민트 (ICE)", 3500, "TEA"),
            product(56L, "복숭아아이스티", 3500, "TEA"),
            product(57L, "유자차", 3900, "TEA"),
            // ===== 디저트 / 베이커리 =====
            product(58L, "버터가 쫀득해떡", 2500, "DESSERT"),
            product(59L, "내 맘대로 더블 젤라또 (딸기&요거트)", 5500, "DESSERT"),
            product(60L, "내 맘대로 더블 젤라또 (초코&말차)", 5500, "DESSERT"),
            product(61L, "딸기 크림치즈 쫀득빵", 3800, "BAKERY"),
            product(62L, "초코젤라또크로플", 4500, "BAKERY"),
            product(63L, "마카다미아 쿠키", 2500, "DESSERT"),
            product(64L, "초콜릿칩 쿠키", 2500, "DESSERT"),
            product(65L, "플레인크로플", 3500, "BAKERY"),
            product(66L, "크로크무슈", 4500, "BAKERY"),
            product(67L, "치즈 케익", 5500, "DESSERT"),
            product(68L, "초코무스 케익", 5500, "DESSERT"),
            product(69L, "티라미수 케익", 5500, "DESSERT"),
            product(70L, "허니브레드", 7500, "BAKERY"),
            // ===== MD 상품 =====
            product(71L, "작지만 강력한 마루 자석 세트(2종 랜덤)", 8000, "MD"),
            product(72L, "나랑 같이 날아 츄! 우주선 빨대 텀블러", 19800, "MD"),
            product(73L, "나랑 같이 가자 츄! 하츄핑 실리콘 가방", 15000, "MD"),
            product(74L, "나랑 같이 마셔 츄! 콜드컵 키링 텀블러", 12000, "MD"),
            product(75L, "나랑 같이 냠냠 츄! 로켓 멀티통", 9800, "MD"),
            product(76L, "나랑 같이 놀자 츄! 카메라 빔 키링", 8800, "MD"),
            product(77L, "나랑 같이 달아 츄! 랜덤 티니핑 파츠 3종", 6000, "MD"),
            product(78L, "캐치!티니핑 하츄핑 랜덤 피규어", 5000, "MD"),
        )

    private val cartItems = linkedMapOf<Long, CartEntry>()
    private var nextCartItemId = 1L

    @Synchronized
    fun getProducts(
        page: Int,
        size: Int,
        category: String?,
    ): ProductPageResponseDto {
        val filtered =
            if (category.isNullOrBlank()) {
                products
            } else {
                products.filter { it.category == category }
            }
        return filtered.toProductPageResponse(page, size)
    }

    @Synchronized
    fun getProduct(id: Long): ProductResponseDto? = products.find { it.id == id }

    @Synchronized
    fun getCartItems(
        page: Int,
        size: Int,
    ): CartPageResponseDto {
        val allItems =
            cartItems.values.mapNotNull { entry ->
                products.find { it.id == entry.productId }?.let { product ->
                    CartItemResponseDto(entry.id, entry.quantity, product)
                }
            }
        return allItems.toCartPageResponse(page, size)
    }

    @Synchronized
    fun getCartItemsCount(): Int = cartItems.size

    @Synchronized
    fun addCartItem(
        productId: Long,
        quantity: Int,
    ) {
        val existing = cartItems.values.find { it.productId == productId }
        if (existing != null) {
            existing.quantity += quantity
            return
        }
        cartItems[nextCartItemId] = CartEntry(nextCartItemId, productId, quantity)
        nextCartItemId += 1
    }

    @Synchronized
    fun updateCartItem(
        id: Long,
        quantity: Int,
    ) {
        val target = cartItems[id] ?: return
        if (quantity <= 0) {
            cartItems.remove(id)
            return
        }
        target.quantity = quantity
    }

    @Synchronized
    fun deleteCartItem(id: Long) {
        cartItems.remove(id)
    }

    @Synchronized
    fun order(cartItemIds: List<Long>) {
        cartItemIds.forEach(cartItems::remove)
    }

    private fun product(
        id: Long,
        name: String,
        price: Int,
        category: String,
    ): ProductResponseDto =
        ProductResponseDto(
            id = id,
            name = name,
            price = price,
            imageUrl = "https://picsum.photos/seed/$id/640/640",
            category = category,
        )
}

private fun List<ProductResponseDto>.toProductPageResponse(
    page: Int,
    size: Int,
): ProductPageResponseDto {
    val safePage = page.coerceAtLeast(0)
    val safeSize = size.coerceAtLeast(1)
    val totalElements = this.size
    val totalPages = if (totalElements == 0) 1 else ((totalElements - 1) / safeSize) + 1
    val pageItems = drop(safePage * safeSize).take(safeSize)
    return ProductPageResponseDto(
        totalElements = totalElements.toLong(),
        content = pageItems,
        last = safePage >= totalPages - 1,
    )
}

private fun List<CartItemResponseDto>.toCartPageResponse(
    page: Int,
    size: Int,
): CartPageResponseDto {
    val safePage = page.coerceAtLeast(0)
    val safeSize = size.coerceAtLeast(1)
    val totalElements = this.size
    val totalPages = if (totalElements == 0) 1 else ((totalElements - 1) / safeSize) + 1
    val pageItems = drop(safePage * safeSize).take(safeSize)
    return CartPageResponseDto(
        totalElements = totalElements.toLong(),
        totalPages = totalPages,
        size = safeSize,
        content = pageItems,
        number = safePage,
    )
}

private data class CartEntry(
    val id: Long,
    val productId: Long,
    var quantity: Int,
)
