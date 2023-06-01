package woowacourse.shopping.presentation.mapper

import woowacourse.shopping.presentation.model.CardModel
import woowacouse.shopping.model.card.Card

fun Card.toUIModel(): CardModel = CardModel(id, name, number, cvc)

fun CardModel.toModel(): Card = Card(id, name, number, cvc)
