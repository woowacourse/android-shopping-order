package woowacourse.shopping.data.cart.source

import io.mockk.every
import io.mockk.mockk
import okhttp3.Headers
import okhttp3.ResponseBody.Companion.toResponseBody
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import retrofit2.Response
import woowacourse.shopping.data.product.source.NetworkProduct

internal class DefaultNetworkCartItemDataSourceTest {

    private lateinit var cartItemRemoteService: CartItemRemoteService
    private lateinit var sut: DefaultNetworkCartItemDataSource

    @BeforeEach
    fun setUp() {
        cartItemRemoteService = mockk()
        sut = DefaultNetworkCartItemDataSource(cartItemRemoteService)
    }

    @Test
    fun `장바구니 아이템들을 네트워크로부터 불러올 때 응답이 성공적이지 않으면 Throwable 객체를 가진 실패한 Result 객체를 받는다`() {
        val errorResponse = Response.error<List<NetworkCartItem>>(400, "".toResponseBody())
        every { cartItemRemoteService.requestCartItems().execute() } returns errorResponse

        val result = sut.loadCartItems()

        assertAll(
            { assertThat(result.isFailure).isTrue },
            { assertThat(result.exceptionOrNull()).isNotNull }
        )
    }

    @Test
    fun `장바구니 아이템들을 네트워크로부터 불러올 때 응답이 성공적이면 NetworkCartItem의 리스트 객체를 가진 성공한 Result 객체를 받는다`() {
        val dummyNetworkCartItems = listOf(
            NetworkCartItem(
                1L,
                1,
                NetworkProduct(1L, "치킨", 10000, "http://example.com/chicken.jpg")
            )
        )
        val successResponse = Response.success<List<NetworkCartItem>>(200, dummyNetworkCartItems)
        every { cartItemRemoteService.requestCartItems().execute() } returns successResponse

        val result = sut.loadCartItems()

        assertAll(
            { assertThat(result.isSuccess).isTrue },
            { assertThat(result.getOrThrow()).isEqualTo(dummyNetworkCartItems) }
        )
    }

    @Test
    fun `장바구니 아이템 저장을 네트워크를 통해 요청할 때 응답이 성공적이지 않으면 Throwable 객체를 가진 실패한 Result 객체를 받는다`() {
        val errorResponse = Response.error<Unit>(400, "".toResponseBody())
        every { cartItemRemoteService.requestToSave(any()).execute() } returns errorResponse

        val anyProductId = 1L
        val result = sut.saveCartItem(anyProductId)

        assertAll(
            { assertThat(result.isFailure).isTrue },
            { assertThat(result.exceptionOrNull()).isNotNull }
        )
    }

    @Test
    fun `장바구니 아이템 저장을 네트워크를 통해 요청할 때 응답이 성공적이면 저장된 장바구니 아이템의 아이디를 가진 성공한 Result 객체를 받는다`() {
        val successResponse =
            Response.success<Unit>(Unit, Headers.headersOf("Location", "/cart-items/1"))
        every { cartItemRemoteService.requestToSave(any()).execute() } returns successResponse

        val anyProductId = 1L
        val result = sut.saveCartItem(anyProductId)

        assertAll(
            { assertThat(result.isSuccess).isTrue },
            { assertThat(result.getOrThrow()).isEqualTo(1) }
        )
    }

    @Test
    fun `장바구니 아이템의 수량 변경을 네트워크를 통해 요청할 때 응답이 성공적이지 않으면 Throwable 객체를 가진 실패한 Result 객체를 받는다`() {
        val errorResponse = Response.error<Unit>(400, "".toResponseBody())
        every {
            cartItemRemoteService.requestToUpdateQuantity(any(), any()).execute()
        } returns errorResponse

        val result = sut.updateCartItemQuantity(1L, 10)

        assertAll(
            { assertThat(result.isFailure).isTrue },
            { assertThat(result.exceptionOrNull()).isNotNull }
        )
    }

    @Test
    fun `장바구니 아이템의 수량 변경을 네트워크를 통해 요청할 때 응답이 성공적이면 성공한 Result 객체를 받는다`() {
        val successResponse = Response.success<Unit>(200, Unit)
        every {
            cartItemRemoteService.requestToUpdateQuantity(any(), any()).execute()
        } returns successResponse

        val result = sut.updateCartItemQuantity(1L, 10)

        assertThat(result.isSuccess).isTrue
    }

    @Test
    fun `장바구니 아이템 삭제를 네트워크를 통해 요청할 때 응답이 성공적이지 않으면 Throwable 객체를 가진 실패한 Result 객체를 받는다`() {
        val errorResponse = Response.error<Unit>(400, "".toResponseBody())
        every { cartItemRemoteService.requestToDelete(any()).execute() } returns errorResponse

        val result = sut.deleteCartItem(1L)

        assertAll(
            { assertThat(result.isFailure).isTrue },
            { assertThat(result.exceptionOrNull()).isNotNull }
        )
    }

    @Test
    fun `장바구니 아이템 삭제를 네트워크를 통해 요청할 때 응답이 성공적이면 성공한 Result 객체를 받는다`() {
        val successResponse = Response.success<Unit>(200, Unit)
        every { cartItemRemoteService.requestToDelete(any()).execute() } returns successResponse

        val result = sut.deleteCartItem(1L)

        assertThat(result.isSuccess).isTrue
    }
}
