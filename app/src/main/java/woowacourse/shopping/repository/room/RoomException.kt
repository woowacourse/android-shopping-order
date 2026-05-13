package woowacourse.shopping.repository.room

sealed class RoomException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)

sealed class CartRoomException(
    message: String,
    cause: Throwable? = null,
) : RoomException(message, cause)

class CartItemNotFoundException(
    productId: Long,
) : CartRoomException("해당 상품은 장바구니에 존재하지 않습니다. productId=${productId}")
