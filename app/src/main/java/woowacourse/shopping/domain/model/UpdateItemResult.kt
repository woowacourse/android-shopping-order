package woowacourse.shopping.domain.model

sealed class UpdateItemResult {
    data class Success(
        val cart: Cart,
    ) : UpdateItemResult()

    data class Error(
        val message: String,
    ) : UpdateItemResult()
}
