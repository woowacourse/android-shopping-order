package com.example.domain.model

import woowacourse.shopping.feature.order.Discount

interface MerchandiseItem {
    fun use(): Pair<Int, Discount.Condition?>
}
