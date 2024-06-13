package woowacourse.shopping.domain.repository

import java.time.LocalDateTime

interface OrderDateTime {
    fun dateTime(): LocalDateTime
}

class LoadBasedOrderDateTime : OrderDateTime {
    override fun dateTime(): LocalDateTime {
        return LocalDateTime.now()
    }
}

class FakeOrderDateTime : OrderDateTime {
    override fun dateTime(): LocalDateTime = LocalDateTime.of(2021, 1, 1, 0, 0)

}