package com.shopping.repository

import com.shopping.domain.OrderPay

interface PayRepository {
    fun postPay(orderPay: OrderPay, onSuccess: (Long) -> Unit)
}
