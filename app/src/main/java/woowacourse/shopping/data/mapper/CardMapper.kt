package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.model.entity.CardEntity
import woowacouse.shopping.model.card.Card

fun CardEntity.toModel(): Card = Card(id, name, number, cvc)
