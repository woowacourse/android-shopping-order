package woowacourse.shopping.utils.exception

import woowacourse.shopping.view.model.event.ErrorEvent

class OrderException(val event: ErrorEvent = ErrorEvent.NotKnownError) : Exception()
