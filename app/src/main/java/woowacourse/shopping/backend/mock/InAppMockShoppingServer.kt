package woowacourse.shopping.backend.mock

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import woowacourse.shopping.backend.retrofit.dto.CartQuantity
import woowacourse.shopping.backend.retrofit.dto.CartRequest
import woowacourse.shopping.backend.retrofit.dto.OrderInfo
import woowacourse.shopping.backend.retrofit.dto.Pageable
import woowacourse.shopping.backend.retrofit.dto.PageableResponse
import woowacourse.shopping.backend.retrofit.dto.ShoppingCartPageable
import woowacourse.shopping.backend.retrofit.dto.ShoppingCartResponse
import woowacourse.shopping.backend.retrofit.dto.Sort
import woowacourse.shopping.backend.retrofit.dto.coupon.AvailableTimeResponse
import woowacourse.shopping.backend.retrofit.dto.coupon.CouponResponse
import woowacourse.shopping.backend.retrofit.dto.productlist.Content
import woowacourse.shopping.backend.retrofit.dto.productlist.Product
import woowacourse.shopping.backend.retrofit.dto.productlist.ProductResponse
import java.net.HttpURLConnection
import java.net.InetAddress

class InAppMockShoppingServer {
    private val server =
        MockWebServer().apply {
            dispatcher = ShoppingMockDispatcher()
        }

    @Volatile
    private var serverStarted: Boolean = false

    val baseUrl: String
        get() = "http://$LOOPBACK_HOST:$PORT/"

    fun start() {
        if (serverStarted) return
        server.start(loopbackAddress(), PORT)
        serverStarted = true
    }

    fun shutdown() {
        if (!serverStarted) return
        server.shutdown()
        serverStarted = false
    }

    private fun loopbackAddress(): InetAddress = InetAddress.getByAddress(byteArrayOf(127, 0, 0, 1))

    private companion object {
        private const val LOOPBACK_HOST = "127.0.0.1"
        private const val PORT = 8080
    }
}

private val json =
    Json {
        ignoreUnknownKeys = true
    }

private class ShoppingMockDispatcher : Dispatcher() {
    private val repository = MockShoppingRepository()

    override fun dispatch(request: RecordedRequest): MockResponse {
        val url = request.requestUrl ?: return errorResponse(HttpURLConnection.HTTP_BAD_REQUEST)
        return when {
            request.method == "GET" && url.encodedPath == "/products" -> products(url)
            request.method == "GET" && url.pathSegments.size == 2 && url.pathSegments[0] == "products" -> productDetail(url)
            request.method == "POST" && url.encodedPath == "/products" -> addProduct(request)
            request.method == "DELETE" && url.pathSegments.size == 2 && url.pathSegments[0] == "products" -> deleteProduct(url)
            request.method == "GET" && url.encodedPath == "/cart-items" -> cartItems(url)
            request.method == "GET" && url.encodedPath == "/cart-items/counts" -> count()
            request.method == "POST" && url.encodedPath == "/cart-items" -> addCartItem(request)
            request.method == "PATCH" && url.pathSegments.size == 2 && url.pathSegments[0] == "cart-items" -> updateCartItem(url, request)
            request.method == "DELETE" && url.pathSegments.size == 2 && url.pathSegments[0] == "cart-items" -> deleteCartItem(url)
            request.method == "POST" && url.encodedPath == "/orders" -> order(request)
            request.method == "GET" && url.encodedPath == "/coupons" -> coupons()
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

    private fun addProduct(request: RecordedRequest): MockResponse {
        val payload = json.decodeFromString<Product>(request.body.readUtf8())
        repository.addProduct(payload)
        return emptyResponse(HttpURLConnection.HTTP_CREATED)
    }

    private fun deleteProduct(url: HttpUrl): MockResponse {
        val id = url.pathSegments[1].toLongOrNull() ?: return errorResponse(HttpURLConnection.HTTP_BAD_REQUEST)
        repository.deleteProduct(id)
        return emptyResponse(HttpURLConnection.HTTP_NO_CONTENT)
    }

    private fun cartItems(url: HttpUrl): MockResponse {
        val page = url.queryParameter("page")?.toIntOrNull() ?: 0
        val size = url.queryParameter("size")?.toIntOrNull() ?: 20
        return jsonResponse(repository.getCartItems(page, size))
    }

    private fun count(): MockResponse = jsonResponse(repository.getCartItemsCount())

    private fun addCartItem(request: RecordedRequest): MockResponse {
        val payload = json.decodeFromString<CartRequest>(request.body.readUtf8())
        repository.addCartItem(payload.productId, payload.quantity)
        return emptyResponse(HttpURLConnection.HTTP_CREATED)
    }

    private fun updateCartItem(
        url: HttpUrl,
        request: RecordedRequest,
    ): MockResponse {
        val id = url.pathSegments[1].toIntOrNull() ?: return errorResponse(HttpURLConnection.HTTP_BAD_REQUEST)
        val payload = json.decodeFromString<CartQuantity>(request.body.readUtf8())
        repository.updateCartItem(id, payload.quantity)
        return emptyResponse(HttpURLConnection.HTTP_OK)
    }

    private fun deleteCartItem(url: HttpUrl): MockResponse {
        val id = url.pathSegments[1].toIntOrNull() ?: return errorResponse(HttpURLConnection.HTTP_BAD_REQUEST)
        repository.deleteCartItem(id)
        return emptyResponse(HttpURLConnection.HTTP_NO_CONTENT)
    }

    private fun order(request: RecordedRequest): MockResponse {
        val payload = json.decodeFromString<OrderInfo>(request.body.readUtf8())
        repository.order(payload.cartItemIds)
        return emptyResponse(HttpURLConnection.HTTP_OK)
    }

    private fun coupons(): MockResponse = jsonResponse(repository.getCoupons())

    private fun jsonResponse(body: Any): MockResponse =
        MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setHeader("Content-Type", "application/json")
            .setBody(
                when (body) {
                    is ProductResponse -> json.encodeToString(body)
                    is Product -> json.encodeToString(body)
                    is ShoppingCartResponse -> json.encodeToString(body)
                    is CartQuantity -> json.encodeToString(body)
                    is List<*> ->
                        when (body.firstOrNull()) {
                            null, is CouponResponse -> json.encodeToString(body.filterIsInstance<CouponResponse>())
                            else -> error("Unsupported list response type: ${body.firstOrNull()!!::class.java.simpleName}")
                        }
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
        mutableListOf(
            product(
                1,
                "꿀수박주스",
                4500,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20260429172504_1777451104073_ubR01Dh_wM.png",
                "SEASON",
            ),
            product(
                2,
                "수박소르베 밀키 스무디",
                5500,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20260429172642_1777451202647_pSL9jKZaL6.png",
                "SMOOTHIE",
            ),
            product(
                3,
                "수박 리치코코 슬러시",
                5300,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20260429172827_1777451307817_3OM_KV_54e.png",
                "SMOOTHIE",
            ),
            product(
                4,
                "파인망고코코 스무디",
                5300,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20260429173000_1777451400716_gEDa5liT8h.png",
                "SMOOTHIE",
            ),
            product(
                5,
                "자몽 톡톡 스무디",
                4900,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20260429173124_1777451484221_DbGYiTq_EF.png",
                "SMOOTHIE",
            ),
            product(
                6,
                "제로 레몬말차 아이스티",
                3900,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20260429173244_1777451564019_W1OpjQHJz4.png",
                "TEA",
            ),
            product(
                7,
                "저당 꿀배 XO야쿠르트",
                4500,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20260401192153_1775038913170_2UFRSEFHv0.png",
                "BEVERAGE",
            ),
            product(
                8,
                "귤 톡톡 젤리스무디",
                4900,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20250320003615_1742398575781_Dy89tEHQTL.jpg",
                "SMOOTHIE",
            ),
            product(
                9,
                "왕메가카페라떼",
                4500,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20250320004326_1742399006488_yI4cvwXy6N.jpg",
                "COFFEE",
            ),
            product(
                10,
                "디카페인 왕메가카페라떼",
                4800,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20250320004527_1742399127150_aZXw3Wbf4H.jpg",
                "DECAFFEINE",
            ),
            product(
                11,
                "디카페인 라이트 바닐라 아몬드라떼",
                4500,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20250402185107_1743587467742_jeeQmgRshX.jpg",
                "DECAFFEINE",
            ),
            product(
                12,
                "왕메가사과유자",
                4500,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20250320004730_1742399250273_UPT0oK5fSb.jpg",
                "TEA",
            ),
            product(
                13,
                "왕메가헛개리카노",
                3900,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20250320001838_1742397518902_MCGDHqjxXi.jpg",
                "COFFEE",
            ),
            product(
                14,
                "(HOT)디카페인 헛개리카노",
                2800,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20250320002019_1742397619030_g5iEBTRsp7.jpg",
                "DECAFFEINE",
            ),
            product(
                15,
                "코코넛 커피 스무디",
                5300,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20240610132830_1717993710301_PxTeTM9Suh.jpg",
                "SMOOTHIE",
            ),
            product(
                16,
                "딸기주스",
                4500,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20240610114741_1717987661472_LqJw6WITHO.jpg",
                "JUICE",
            ),
            product(
                17,
                "딸기바나나주스",
                4500,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20240610114817_1717987697259_APSvRawmJi.jpg",
                "JUICE",
            ),
            product(
                18,
                "디카페인 에스프레소",
                2500,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20240610110159_1717984919074_jg4RYBdr_J.jpg",
                "DECAFFEINE",
            ),
            product(
                19,
                "디카페인 카페라떼",
                3500,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20240610132005_1717993205762_9xKCxaSb9P.jpg",
                "DECAFFEINE",
            ),
            product(
                20,
                "디카페인 카푸치노",
                3500,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20240610132018_1717993218605_64Ij4AhmCE.jpg",
                "DECAFFEINE",
            ),
            product(
                21,
                "디카페인 바닐라라떼",
                4200,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20240610113234_1717986754069_tSPQqoiPj6.jpg",
                "DECAFFEINE",
            ),
            product(
                22,
                "디카페인 헤이즐넛 라떼",
                4200,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20240610132034_1717993234457_aGDHQ0U0Ap.jpg",
                "DECAFFEINE",
            ),
            product(
                23,
                "디카페인 카라멜마끼아또",
                4500,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20240610131917_1717993157515_k1yeakRwwv.jpg",
                "DECAFFEINE",
            ),
            product(
                24,
                "오레오초코라떼",
                4500,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20240610132638_1717993598875_AQ4q6GUaJD.jpg",
                "LATTE",
            ),
            product(
                25,
                "토피넛라떼",
                4500,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20240610132535_1717993535868_Dk0SpiNnAo.jpg",
                "LATTE",
            ),
            product(
                26,
                "흑당버블밀크티라떼",
                4500,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20240610112956_1717986596859_dSAYDVVaV2.jpg",
                "LATTE",
            ),
            product(
                27,
                "핫초코",
                3500,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20240610110045_1717984845295_sGoEnUlQpO.jpg",
                "BEVERAGE",
            ),
            product(
                28,
                "녹차라떼",
                3900,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20240610104456_1717983896154_TxgXQHRHiM.jpg",
                "LATTE",
            ),
            product(
                29,
                "로얄밀크티라떼",
                3900,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20240610105616_1717984576627_IStTaWZY3_.jpg",
                "LATTE",
            ),
            product(
                30,
                "흑당라떼",
                4500,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20240610113159_1717986719673_UjpcQYhtmh.jpg",
                "LATTE",
            ),
            product(
                31,
                "흑당밀크티라떼",
                4500,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20240610113123_1717986683549_Q8E9tuNYJL.jpg",
                "LATTE",
            ),
            product(
                32,
                "흑당버블라떼",
                4500,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20240610113023_1717986623453_FuG0kt97Jg.jpg",
                "LATTE",
            ),
            product(
                33,
                "카페모카",
                3500,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20240610105838_1717984718108_ZB6aalHqIU.jpg",
                "COFFEE",
            ),
            product(
                34,
                "카푸치노",
                3500,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20240610105852_1717984732750_WEt0KXVcnQ.jpg",
                "COFFEE",
            ),
            product(
                35,
                "콜드브루라떼",
                3500,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20240610105923_1717984763591_cKSGllWUzt.jpg",
                "COFFEE",
            ),
            product(
                36,
                "콜드브루오리지널",
                3500,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20240610105941_1717984781078_rdCUXhwycL.jpg",
                "COFFEE",
            ),
            product(
                37,
                "헤이즐넛라떼",
                3500,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20240610110117_1717984877061_sLiVRNPq6J.jpg",
                "COFFEE",
            ),
            product(
                38,
                "헤이즐넛아메리카노",
                2500,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20240610110133_1717984893137_AqJAuHaq0t.jpg",
                "COFFEE",
            ),
            product(
                39,
                "꿀아메리카노",
                3500,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20240610133054_1717993854434_bTty_9u3br.jpg",
                "COFFEE",
            ),
            product(
                40,
                "바닐라라떼",
                3500,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20240610132249_1717993369864_VIG6IR2byt.jpg",
                "COFFEE",
            ),
            product(
                41,
                "딸기요거트스무디",
                5300,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20240610132117_1717993277823_KRGDVA4aeJ.jpg",
                "SMOOTHIE",
            ),
            product(
                42,
                "딸기퐁크러쉬",
                5500,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20240610132132_1717993292542_TOXDd7N_72.jpg",
                "FRAPPE",
            ),
            product(
                43,
                "리얼초코프라페",
                4500,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20240610132157_1717993317456_RCHCZKkudt.jpg",
                "FRAPPE",
            ),
            product(
                44,
                "망고요거트스무디",
                5300,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20240610132210_1717993330781_04rKlgIKk6.jpg",
                "SMOOTHIE",
            ),
            product(
                45,
                "민트프라페",
                4500,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20240610115353_1717988033471_n8WL4rST4C.jpg",
                "FRAPPE",
            ),
            product(
                46,
                "바나나퐁크러쉬",
                5500,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20240610132238_1717993358631_MGPEpkvNh1.jpg",
                "FRAPPE",
            ),
            product(
                47,
                "초코허니퐁크러쉬",
                5500,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20240610132651_1717993611266_QFRonrkBzf.jpg",
                "FRAPPE",
            ),
            product(
                48,
                "커피프라페",
                4500,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20240610132804_1717993684465__jH6f5a2VM.jpg",
                "FRAPPE",
            ),
            product(
                49,
                "캐모마일 (HOT)",
                3500,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20240610105908_1717984748214_qc34s7Vkq5.jpg",
                "TEA",
            ),
            product(
                50,
                "페퍼민트 (HOT)",
                3500,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20240610110026_1717984826888_gfP6LsxEV3.jpg",
                "TEA",
            ),
            product(
                51,
                "녹차 (ICE)",
                3500,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20240610112857_1717986537987_H1q4z1rnt2.jpg",
                "TEA",
            ),
            product(
                52,
                "사과유자차",
                3900,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20240610132332_1717993412234_5kkTR1DkNH.jpg",
                "TEA",
            ),
            product(
                53,
                "얼그레이",
                3500,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20240610132511_1717993511025_ChoqheNJQf.jpg",
                "TEA",
            ),
            product(
                54,
                "캐모마일 (ICE)",
                3500,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20240610132753_1717993673758__j_omi1oWY.jpg",
                "TEA",
            ),
            product(
                55,
                "페퍼민트 (ICE)",
                3500,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20240610115138_1717987898720_OdAWvoBqNh.jpg",
                "TEA",
            ),
            product(
                56,
                "복숭아아이스티",
                3500,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20240610132446_1717993486995_hvfli27vPn.jpg",
                "TEA",
            ),
            product(
                57,
                "유자차",
                3900,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20240610105725_1717984645282_YfXeYsJzVV.jpg",
                "TEA",
            ),
            product(
                58,
                "버터가 쫀득해떡",
                2500,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20260407215938_1775566778053_U6yDWuVmpF.jpg",
                "DESSERT",
            ),
            product(
                59,
                "내 맘대로 더블 젤라또 (딸기&요거트)",
                5500,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20260407171036_1775549436955_KJ8TwVQHI3.png",
                "DESSERT",
            ),
            product(
                60,
                "내 맘대로 더블 젤라또 (초코&말차)",
                5500,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20260407171228_1775549548671_hyOiR1Wk4E.png",
                "DESSERT",
            ),
            product(
                61,
                "딸기 크림치즈 쫀득빵",
                3800,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20251226202655_1766748415087_NsW1Ifwl8a.png",
                "BAKERY",
            ),
            product(
                62,
                "초코젤라또크로플",
                4500,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20240228221348_1709126028809_6VjAVPuxOP.jpg",
                "BAKERY",
            ),
            product(
                63,
                "마카다미아 쿠키",
                2500,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20220704113933_1656902373934_smXcsfdrHY.jpg",
                "DESSERT",
            ),
            product(
                64,
                "초콜릿칩 쿠키",
                2500,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20220704114022_1656902422672_HECxBh1Vuk.jpg",
                "DESSERT",
            ),
            product(
                65,
                "플레인크로플",
                3500,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20220701141927_1656652767760_yc4rsWS5yT.jpg",
                "BAKERY",
            ),
            product(
                66,
                "크로크무슈",
                4500,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20230508143055_1683523855357_b1oieG5cId.png",
                "BAKERY",
            ),
            product(
                67,
                "치즈 케익",
                5500,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20240719183644_1721381804706_g8I_YcFpc7.jpg",
                "DESSERT",
            ),
            product(
                68,
                "초코무스 케익",
                5500,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20220704112422_1656901462139_JSxU5sBxAN.jpg",
                "DESSERT",
            ),
            product(
                69,
                "티라미수 케익",
                5500,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20220704113333_1656902013629_aEHrGt8IO6.jpg",
                "DESSERT",
            ),
            product(
                70,
                "허니브레드",
                7500,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20220704113432_1656902072749_liQCQrLx0R.jpg",
                "BAKERY",
            ),
            product(
                71,
                "작지만 강력한 마루 자석 세트(2종 랜덤)",
                8000,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20250423235659_1745420219764_nfCsetfWOZ.jpg",
                "MD",
            ),
            product(
                72,
                "나랑 같이 날아 츄! 우주선 빨대 텀블러",
                19800,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20241023211612_1729685772681_nAZ0gCnL21.jpg",
                "MD",
            ),
            product(
                73,
                "나랑 같이 가자 츄! 하츄핑 실리콘 가방",
                15000,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20241023211908_1729685948585_e8lASrT0Uy.jpg",
                "MD",
            ),
            product(
                74,
                "나랑 같이 마셔 츄! 콜드컵 키링 텀블러",
                12000,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20241023211823_1729685903747_KQC1QUAgR3.jpg",
                "MD",
            ),
            product(
                75,
                "나랑 같이 냠냠 츄! 로켓 멀티통",
                9800,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20241023212015_1729686015557_fh9lJdKwGu.jpg",
                "MD",
            ),
            product(
                76,
                "나랑 같이 놀자 츄! 카메라 빔 키링",
                8800,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20241023212054_1729686054320_MdiR0iMLec.jpg",
                "MD",
            ),
            product(
                77,
                "나랑 같이 달아 츄! 랜덤 티니핑 파츠 3종",
                6000,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20241023211733_1729685853124_zxyhgo3unG.jpg",
                "MD",
            ),
            product(
                78,
                "캐치!티니핑 하츄핑 랜덤 피규어",
                5000,
                "https://img.79plus.co.kr/megahp/manager/upload/menu/20240806193504_1722940504072_335GhVE_6s.jpg",
                "MD",
            ),
        )

    private val cartItems =
        linkedMapOf(
            1 to CartEntry(id = 1, productId = 2L, quantity = 1),
            2 to CartEntry(id = 2, productId = 9L, quantity = 2),
        )
    private var nextCartItemId = 3

    @Synchronized
    fun getProducts(
        page: Int,
        size: Int,
        category: String?,
    ): ProductResponse {
        val filtered =
            if (category.isNullOrBlank()) {
                products
            } else {
                products.filter { it.category == category }
            }
        return filtered.toPagedProductResponse(page, size)
    }

    @Synchronized
    fun getProduct(id: Long): Product? = products.find { it.id == id }

    @Synchronized
    fun addProduct(product: Product) {
        products.removeAll { it.id == product.id }
        products.add(product)
    }

    @Synchronized
    fun deleteProduct(id: Long) {
        products.removeAll { it.id == id }
    }

    @Synchronized
    fun getCartItems(
        page: Int,
        size: Int,
    ): ShoppingCartResponse {
        val allItems =
            cartItems.values.mapNotNull { entry ->
                products.find { it.id == entry.productId }?.let { product ->
                    Content(
                        id = entry.id.toLong(),
                        product = product,
                        quantity = entry.quantity,
                    )
                }
            }
        return allItems.toPagedCartResponse(page, size)
    }

    @Synchronized
    fun getCartItemsCount(): CartQuantity = CartQuantity(quantity = cartItems.values.sumOf { it.quantity })

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
    fun order(cartItemIds: List<Long>) {
        cartItemIds.forEach { cartItemId ->
            cartItems.remove(cartItemId.toInt())
        }
    }

    fun getCoupons(): List<CouponResponse> =
        listOf(
            fixedCoupon(1, "FIXED1000", "1,000원 할인 쿠폰", 1_000, 10_000),
            fixedCoupon(2, "FIXED2000", "2,000원 할인 쿠폰", 2_000, 15_000),
            fixedCoupon(3, "FIXED3000", "3,000원 할인 쿠폰", 3_000, 20_000),
            fixedCoupon(4, "FIXED5000", "5,000원 할인 쿠폰", 5_000, 30_000),
            fixedCoupon(5, "FIXED10000", "10,000원 할인 쿠폰", 10_000, 50_000),
            percentageCoupon(6, "PERCENT5", "5% 할인 쿠폰", 5, "00:00:00", "23:59:59"),
            percentageCoupon(7, "PERCENT10", "10% 할인 쿠폰", 10, "00:00:00", "23:59:59"),
            percentageCoupon(8, "LUNCH15", "점심시간 15% 할인 쿠폰", 15, "11:00:00", "14:00:00"),
            percentageCoupon(9, "DINNER20", "저녁시간 20% 할인 쿠폰", 20, "17:00:00", "21:00:00"),
            percentageCoupon(10, "NIGHT30", "심야 30% 할인 쿠폰", 30, "22:00:00", "23:59:59"),
            buyXGetYCoupon(11, "BUY1GET1", "1+1 쿠폰", 1, 1),
            buyXGetYCoupon(12, "BUY2GET1", "2+1 쿠폰", 2, 1),
            buyXGetYCoupon(13, "BUY3GET1", "3+1 쿠폰", 3, 1),
            buyXGetYCoupon(14, "BUY3GET2", "3+2 쿠폰", 3, 2),
            buyXGetYCoupon(15, "BUY5GET3", "5+3 쿠폰", 5, 3),
            freeShippingCoupon(16, "FREESHIP0", "무료 배송 쿠폰 (조건 없음)", 0),
            freeShippingCoupon(17, "FREESHIP10000", "10,000원 이상 구매 시 무료 배송", 10_000),
            freeShippingCoupon(18, "FREESHIP20000", "20,000원 이상 구매 시 무료 배송", 20_000),
            freeShippingCoupon(19, "FREESHIP30000", "30,000원 이상 구매 시 무료 배송", 30_000),
            freeShippingCoupon(20, "FREESHIP50000", "50,000원 이상 구매 시 무료 배송", 50_000),
        )

    private fun fixedCoupon(
        id: Long,
        code: String,
        description: String,
        discount: Int,
        minimumAmount: Int,
    ): CouponResponse =
        CouponResponse(
            id = id,
            code = code,
            description = description,
            expirationDate = COUPON_EXPIRATION_DATE,
            discountType = "fixed",
            discount = discount,
            minimumAmount = minimumAmount,
        )

    private fun percentageCoupon(
        id: Long,
        code: String,
        description: String,
        discount: Int,
        start: String,
        end: String,
    ): CouponResponse =
        CouponResponse(
            id = id,
            code = code,
            description = description,
            expirationDate = COUPON_EXPIRATION_DATE,
            discountType = "percentage",
            discount = discount,
            availableTime = AvailableTimeResponse(start = start, end = end),
        )

    private fun buyXGetYCoupon(
        id: Long,
        code: String,
        description: String,
        buyQuantity: Int,
        getQuantity: Int,
    ): CouponResponse =
        CouponResponse(
            id = id,
            code = code,
            description = description,
            expirationDate = COUPON_EXPIRATION_DATE,
            discountType = "buyXgetY",
            buyQuantity = buyQuantity,
            getQuantity = getQuantity,
        )

    private fun freeShippingCoupon(
        id: Long,
        code: String,
        description: String,
        minimumAmount: Int,
    ): CouponResponse =
        CouponResponse(
            id = id,
            code = code,
            description = description,
            expirationDate = COUPON_EXPIRATION_DATE,
            discountType = "freeShipping",
            minimumAmount = minimumAmount,
        )

    private fun product(
        id: Long,
        name: String,
        price: Int,
        imageUrl: String,
        category: String,
    ): Product =
        Product(
            category = category,
            id = id,
            imageUrl = imageUrl,
            name = name,
            price = price,
        )
}

private fun List<Product>.toPagedProductResponse(
    page: Int,
    size: Int,
): ProductResponse {
    val safePage = page.coerceAtLeast(0)
    val safeSize = size.coerceAtLeast(1)
    val totalElements = this.size.toLong()
    val totalPages = if (isEmpty()) 1 else ((this.size - 1) / safeSize) + 1
    val pageItems = drop(safePage * safeSize).take(safeSize)
    val defaultSort = Sort(empty = true, sorted = false, unsorted = true)
    return ProductResponse(
        totalElements = totalElements,
        totalPages = totalPages,
        size = safeSize,
        content =
            pageItems.map { product ->
                Pageable(
                    id = product.id,
                    name = product.name,
                    price = product.price,
                    imageUrl = product.imageUrl,
                    category = product.category,
                )
            },
        number = safePage,
        sort = defaultSort,
        pageable =
            PageableResponse(
                offset = safePage.toLong() * safeSize,
                sort = defaultSort,
                paged = true,
                pageNumber = safePage,
                pageSize = safeSize,
                unpaged = false,
            ),
        numberOfElements = pageItems.size,
        first = safePage == 0,
        last = safePage >= totalPages - 1,
        empty = pageItems.isEmpty(),
    )
}

private fun List<Content>.toPagedCartResponse(
    page: Int,
    size: Int,
): ShoppingCartResponse {
    val safePage = page.coerceAtLeast(0)
    val safeSize = size.coerceAtLeast(1)
    val totalElements = this.size.toLong()
    val totalPages = if (isEmpty()) 1 else ((this.size - 1) / safeSize) + 1
    val pageItems = drop(safePage * safeSize).take(safeSize)
    val defaultSort = Sort(empty = true, sorted = false, unsorted = true)
    return ShoppingCartResponse(
        totalElements = totalElements,
        totalPages = totalPages,
        size = safeSize,
        content = pageItems,
        number = safePage,
        numberOfElements = pageItems.size,
        first = safePage == 0,
        last = safePage >= totalPages - 1,
        empty = pageItems.isEmpty(),
        pageable =
            ShoppingCartPageable(
                offset = safePage.toLong() * safeSize,
                pageNumber = safePage,
                pageSize = safeSize,
                paged = true,
                sort = defaultSort,
                unpaged = false,
            ),
        sort = defaultSort,
    )
}

private data class CartEntry(
    val id: Int,
    val productId: Long,
    var quantity: Int,
)

private const val COUPON_EXPIRATION_DATE = "2026-12-31"
