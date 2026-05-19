package woowacourse.shopping.domain

sealed interface OrderResult {
    data object Success : OrderResult
    data object AuthExpired : OrderResult
    data object ServerError : OrderResult
    data object NetworkError : OrderResult
}
