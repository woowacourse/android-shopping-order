package woowacourse.shopping.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.HttpException
import retrofit2.Response
import woowacourse.shopping.domain.exception.NetworkError

@RunWith(AndroidJUnit4::class)
class NetworkResultHandlerTest {
    private val handler = NetworkResultHandler()

    private fun createHttpException(code: Int): HttpException {
        val response =
            Response.error<String>(
                code,
                "Error $code".toResponseBody("application/json".toMediaTypeOrNull()),
            )
        return HttpException(response)
    }

    @Test
    fun `Http_400은_BadRequestError로_매핑된다`() {
        val result = handler.handleException<String>(createHttpException(400))
        assertTrue(result.exceptionOrNull() is NetworkError.HttpError.BadRequestError)
    }

    @Test
    fun `Http_401은_AuthenticationError로_매핑된다`() {
        val result = handler.handleException<String>(createHttpException(401))
        assertTrue(result.exceptionOrNull() is NetworkError.HttpError.AuthenticationError)
    }

    @Test
    fun `Http_403은_AuthorizationError로_매핑된다`() {
        val result = handler.handleException<String>(createHttpException(403))
        assertTrue(result.exceptionOrNull() is NetworkError.HttpError.AuthorizationError)
    }

    @Test
    fun `Http_404는_NotFoundError로_매핑된다`() {
        val result = handler.handleException<String>(createHttpException(404))
        assertTrue(result.exceptionOrNull() is NetworkError.HttpError.NotFoundError)
    }

    @Test
    fun `Http_500은_ServerError로_매핑된다`() {
        val result = handler.handleException<String>(createHttpException(500))
        assertTrue(result.exceptionOrNull() is NetworkError.HttpError.ServerError)
    }

    @Test
    fun `MissingLocationHeaderError는_그대로_반환된다`() {
        val error = NetworkError.MissingLocationHeaderError
        val result = handler.handleException<String>(error)
        assertSame(error, result.exceptionOrNull())
    }

    @Test
    fun `기타_예외는_UnknownError로_매핑된다`() {
        val result = handler.handleException<String>(IllegalStateException("unexpected"))
        assertTrue(result.exceptionOrNull() is NetworkError.UnknownError)
    }
}
