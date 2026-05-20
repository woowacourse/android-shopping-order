package woowacourse.shopping.mock

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import woowacourse.shopping.data.remote.dto.CartItemDto
import woowacourse.shopping.data.remote.dto.CartResponseDto
import woowacourse.shopping.data.remote.dto.ProductResponseDto
import woowacourse.shopping.data.remote.dto.ProductsResponseDto
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
        val id = url.pathSegments[1].toIntOrNull() ?: return errorResponse(HttpURLConnection.HTTP_BAD_REQUEST)
        val product = repository.getProduct(id) ?: return errorResponse(HttpURLConnection.HTTP_NOT_FOUND)
        return jsonResponse(product)
    }

    private fun cartItems(url: HttpUrl): MockResponse {
        val page = url.queryParameter("page")?.toIntOrNull() ?: 0
        val size = url.queryParameter("size")?.toIntOrNull() ?: 20
        return jsonResponse(repository.getCartItems(page, size))
    }

    private fun count(): MockResponse =
        MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setHeader("Content-Type", "application/json")
            .setBody(repository.getCartItemsCount().toString())

    private fun addCartItem(request: RecordedRequest): MockResponse {
        val payload = json.decodeFromString<AddCartItemPayload>(request.body.readUtf8())
        repository.addCartItem(payload.productId, payload.quantity)
        return emptyResponse(HttpURLConnection.HTTP_CREATED)
    }

    private fun updateCartItem(
        url: HttpUrl,
        request: RecordedRequest,
    ): MockResponse {
        val id = url.pathSegments[1].toIntOrNull() ?: return errorResponse(HttpURLConnection.HTTP_BAD_REQUEST)
        val payload = json.decodeFromString<UpdateCartItemPayload>(request.body.readUtf8())
        repository.updateCartItem(id, payload.quantity)
        return emptyResponse(HttpURLConnection.HTTP_OK)
    }

    private fun deleteCartItem(url: HttpUrl): MockResponse {
        val id = url.pathSegments[1].toIntOrNull() ?: return errorResponse(HttpURLConnection.HTTP_BAD_REQUEST)
        repository.deleteCartItem(id)
        return emptyResponse(HttpURLConnection.HTTP_NO_CONTENT)
    }

    private fun order(request: RecordedRequest): MockResponse {
        val payload = json.decodeFromString<OrderPayload>(request.body.readUtf8())
        repository.order(payload.cartItemIds)
        return emptyResponse(HttpURLConnection.HTTP_OK)
    }

    private fun jsonResponse(body: Any): MockResponse =
        MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setHeader("Content-Type", "application/json")
            .setBody(
                when (body) {
                    is ProductsResponseDto -> json.encodeToString(body)
                    is ProductResponseDto -> json.encodeToString(body)
                    is CartResponseDto -> json.encodeToString(body)
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
            product(1, "꿀수박주스", 4500, "SEASON"),
            product(2, "수박소르베 밀키 스무디", 5500, "SMOOTHIE"),
            product(3, "수박 리치코코 슬러시", 5300, "SMOOTHIE"),
            product(4, "파인망고코코 스무디", 5300, "SMOOTHIE"),
            product(5, "자몽 톡톡 스무디", 4900, "SMOOTHIE"),
            product(6, "제로 레몬말차 아이스티", 3900, "TEA"),
            product(7, "저당 꿀배 XO야쿠르트", 4500, "BEVERAGE"),

            // ===== 왕메가 / 디카페인 =====
            product(8, "귤 톡톡 젤리스무디", 4900, "SMOOTHIE"),
            product(9, "왕메가카페라떼", 4500, "COFFEE"),
            product(10, "디카페인 왕메가카페라떼", 4800, "DECAFFEINE"),
            product(11, "디카페인 라이트 바닐라 아몬드라떼", 4500, "DECAFFEINE"),
            product(12, "왕메가사과유자", 4500, "TEA"),
            product(13, "왕메가헛개리카노", 3900, "COFFEE"),
            product(14, "(HOT)디카페인 헛개리카노", 2800, "DECAFFEINE"),

            // ===== 주스 / 디카페인 =====
            product(15, "코코넛 커피 스무디", 5300, "SMOOTHIE"),
            product(16, "딸기주스", 4500, "JUICE"),
            product(17, "딸기바나나주스", 4500, "JUICE"),
            product(18, "디카페인 에스프레소", 2500, "DECAFFEINE"),

            // ===== 디카페인 음료 =====
            product(19, "디카페인 카페라떼", 3500, "DECAFFEINE"),
            product(20, "디카페인 카푸치노", 3500, "DECAFFEINE"),
            product(21, "디카페인 바닐라라떼", 4200, "DECAFFEINE"),
            product(22, "디카페인 헤이즐넛 라떼", 4200, "DECAFFEINE"),
            product(23, "디카페인 카라멜마끼아또", 4500, "DECAFFEINE"),

            // ===== 시즌 라떼 / 핫 음료 =====
            product(24, "오레오초코라떼", 4500, "LATTE"),
            product(25, "토피넛라떼", 4500, "LATTE"),
            product(26, "흑당버블밀크티라떼", 4500, "LATTE"),
            product(27, "핫초코", 3500, "BEVERAGE"),
            product(28, "녹차라떼", 3900, "LATTE"),
            product(29, "로얄밀크티라떼", 3900, "LATTE"),
            product(30, "흑당라떼", 4500, "LATTE"),
            product(31, "흑당밀크티라떼", 4500, "LATTE"),
            product(32, "흑당버블라떼", 4500, "LATTE"),

            // ===== 에스프레소 클래식 =====
            product(33, "카페모카", 3500, "COFFEE"),
            product(34, "카푸치노", 3500, "COFFEE"),
            product(35, "콜드브루라떼", 3500, "COFFEE"),
            product(36, "콜드브루오리지널", 3500, "COFFEE"),
            product(37, "헤이즐넛라떼", 3500, "COFFEE"),
            product(38, "헤이즐넛아메리카노", 2500, "COFFEE"),
            product(39, "꿀아메리카노", 3500, "COFFEE"),
            product(40, "바닐라라떼", 3500, "COFFEE"),

            // ===== 스무디 / 프라페 =====
            product(41, "딸기요거트스무디", 5300, "SMOOTHIE"),
            product(42, "딸기퐁크러쉬", 5500, "FRAPPE"),
            product(43, "리얼초코프라페", 4500, "FRAPPE"),
            product(44, "망고요거트스무디", 5300, "SMOOTHIE"),
            product(45, "민트프라페", 4500, "FRAPPE"),
            product(46, "바나나퐁크러쉬", 5500, "FRAPPE"),
            product(47, "초코허니퐁크러쉬", 5500, "FRAPPE"),
            product(48, "커피프라페", 4500, "FRAPPE"),

            // ===== 티 =====
            product(49, "캐모마일 (HOT)", 3500, "TEA"),
            product(50, "페퍼민트 (HOT)", 3500, "TEA"),
            product(51, "녹차 (ICE)", 3500, "TEA"),
            product(52, "사과유자차", 3900, "TEA"),
            product(53, "얼그레이", 3500, "TEA"),
            product(54, "캐모마일 (ICE)", 3500, "TEA"),
            product(55, "페퍼민트 (ICE)", 3500, "TEA"),
            product(56, "복숭아아이스티", 3500, "TEA"),
            product(57, "유자차", 3900, "TEA"),

            // ===== 디저트 / 베이커리 =====
            product(58, "버터가 쫀득해떡", 2500, "DESSERT"),
            product(59, "내 맘대로 더블 젤라또 (딸기&요거트)", 5500, "DESSERT"),
            product(60, "내 맘대로 더블 젤라또 (초코&말차)", 5500, "DESSERT"),
            product(61, "딸기 크림치즈 쫀득빵", 3800, "BAKERY"),
            product(62, "초코젤라또크로플", 4500, "BAKERY"),
            product(63, "마카다미아 쿠키", 2500, "DESSERT"),
            product(64, "초콜릿칩 쿠키", 2500, "DESSERT"),
            product(65, "플레인크로플", 3500, "BAKERY"),
            product(66, "크로크무슈", 4500, "BAKERY"),
            product(67, "치즈 케익", 5500, "DESSERT"),
            product(68, "초코무스 케익", 5500, "DESSERT"),
            product(69, "티라미수 케익", 5500, "DESSERT"),
            product(70, "허니브레드", 7500, "BAKERY"),

            // ===== MD 상품 =====
            product(71, "작지만 강력한 마루 자석 세트(2종 랜덤)", 8000, "MD"),
            product(72, "나랑 같이 날아 츄! 우주선 빨대 텀블러", 19800, "MD"),
            product(73, "나랑 같이 가자 츄! 하츄핑 실리콘 가방", 15000, "MD"),
            product(74, "나랑 같이 마셔 츄! 콜드컵 키링 텀블러", 12000, "MD"),
            product(75, "나랑 같이 냠냠 츄! 로켓 멀티통", 9800, "MD"),
            product(76, "나랑 같이 놀자 츄! 카메라 빔 키링", 8800, "MD"),
            product(77, "나랑 같이 달아 츄! 랜덤 티니핑 파츠 3종", 6000, "MD"),
            product(78, "캐치!티니핑 하츄핑 랜덤 피규어", 5000, "MD"),
        )

    private val cartItems = linkedMapOf(1 to CartEntry(id = 1, productId = 2, quantity = 1), 2 to CartEntry(id = 2, productId = 9, quantity = 2))
    private var nextCartItemId = 3

    @Synchronized
    fun getProducts(
        page: Int,
        size: Int,
        category: String?,
    ): ProductsResponseDto {
        val filtered =
            if (category.isNullOrBlank()) {
                products
            } else {
                products.filter { it.category == category }
            }
        return filtered.toPagedResponse(page, size)
    }

    @Synchronized
    fun getProduct(id: Int): ProductResponseDto? = products.find { it.id == id }

    @Synchronized
    fun getCartItems(
        page: Int,
        size: Int,
    ): CartResponseDto {
        val allItems =
            cartItems.values.mapNotNull { entry ->
                products.find { it.id == entry.productId }?.let { product ->
                    CartItemDto(entry.id, entry.quantity, product)
                }
            }
        return allItems.toPagedResponse(page, size)
    }

    @Synchronized
    fun getCartItemsCount(): Int = cartItems.size

    @Synchronized
    fun addCartItem(
        productId: Int,
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
        id: Int,
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
    fun deleteCartItem(id: Int) {
        cartItems.remove(id)
    }

    @Synchronized
    fun order(cartItemIds: List<Int>) {
        cartItemIds.forEach(cartItems::remove)
    }

    private fun product(
        id: Int,
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

private fun List<ProductResponseDto>.toPagedResponse(
    page: Int,
    size: Int,
): ProductsResponseDto {
    val safePage = page.coerceAtLeast(0)
    val safeSize = size.coerceAtLeast(1)
    val totalElements = this.size
    val totalPages = if (totalElements == 0) 1 else ((totalElements - 1) / safeSize) + 1
    val pageItems = drop(safePage * safeSize).take(safeSize)
    return ProductsResponseDto(
        totalElements = totalElements,
        totalPages = totalPages,
        size = safeSize,
        content = pageItems,
        number = safePage,
        numberOfElements = pageItems.size,
        first = safePage == 0,
        last = safePage >= totalPages - 1,
        empty = pageItems.isEmpty(),
    )
}

private fun List<CartItemDto>.toPagedResponse(
    page: Int,
    size: Int,
): CartResponseDto {
    val safePage = page.coerceAtLeast(0)
    val safeSize = size.coerceAtLeast(1)
    val totalElements = this.size
    val totalPages = if (totalElements == 0) 1 else ((totalElements - 1) / safeSize) + 1
    val pageItems = drop(safePage * safeSize).take(safeSize)
    return CartResponseDto(
        totalElements = totalElements,
        totalPages = totalPages,
        size = safeSize,
        content = pageItems,
        number = safePage,
        numberOfElements = pageItems.size,
        first = safePage == 0,
        last = safePage >= totalPages - 1,
        empty = pageItems.isEmpty(),
    )
}

@kotlinx.serialization.Serializable
private data class AddCartItemPayload(
    val productId: Int,
    val quantity: Int,
)

@kotlinx.serialization.Serializable
private data class UpdateCartItemPayload(
    val quantity: Int,
)

@kotlinx.serialization.Serializable
private data class OrderPayload(
    val cartItemIds: List<Int>,
)

private data class CartEntry(
    val id: Int,
    val productId: Int,
    var quantity: Int,
)
