package woowacourse.shopping.data.remote.coupon

import woowacourse.shopping.data.remote.common.RemoteException

sealed class CouponRemoteException(
    message: String,
    cause: Throwable? = null,
) : RemoteException(message, cause)

class CouponNetworkException(
    message: String,
    cause: Throwable,
) : CouponRemoteException(message, cause)

class CouponResponseException(
    val code: Int,
    message: String,
) : CouponRemoteException(message)

class CouponParsingException(
    message: String,
    cause: Throwable,
) : CouponRemoteException(message, cause)
