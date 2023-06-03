package woowacourse.shopping.domain.exception

sealed interface AddOrderException {
    class LackOfPointException(val detailMessage: String) : IllegalArgumentException(detailMessage)
    class ShortageStockException(val detailMessage: String) :
        IllegalArgumentException(detailMessage)
}
