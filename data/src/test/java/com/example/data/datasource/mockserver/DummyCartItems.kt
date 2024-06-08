package com.example.data.datasource.mockserver

import com.example.domain.model.CartItem
import com.example.domain.model.Quantity
import woowacourse.shopping.data.dummy.dummyProducts

val dummyCartItems = List(8) { CartItem(it, dummyProducts[it], Quantity(1)) }
