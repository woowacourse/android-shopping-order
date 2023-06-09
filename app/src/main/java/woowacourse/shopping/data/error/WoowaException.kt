package woowacourse.shopping.data.error

sealed class WoowaException(override val message: String?) : Throwable() {
    class ResponseBodyNull(errorMessage: String) : WoowaException("서버 통신의 response의 body가 널입니다. $errorMessage")
    class ResponseFailure(errorMessage: String) : WoowaException("서버 통신이 적절한 응답에 실패했습니다. $errorMessage")
    class ServerConnectError(errorMessage: String) : WoowaException("서버 통신에 실패했습니다. $errorMessage")
    class DatabaseError : WoowaException("디비에서 오류가 발생했습니다.")
}
