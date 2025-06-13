package woowacourse.shopping.data.account

sealed interface BasicKeyAuthorizationResult {
    data object LoginSuccess : BasicKeyAuthorizationResult

    data object WrongAccountInfo : BasicKeyAuthorizationResult

    data object LoginError : BasicKeyAuthorizationResult
}
