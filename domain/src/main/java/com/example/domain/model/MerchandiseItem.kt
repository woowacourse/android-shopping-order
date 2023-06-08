package com.example.domain.model

interface MerchandiseItem {
    fun use(): Pair<Int, Discount.Condition?>
}
