package woowacourse.shopping.mapper

import com.example.domain.model.MoneySalePolicy.Companion.MoneySale
import woowacourse.shopping.model.MoneySaleUiModel
import woowacourse.shopping.util.toMoneyFormat

fun MoneySale.toPresentation(): MoneySaleUiModel {
    return MoneySaleUiModel(boundary.toMoneyFormat(), saleAmount.toMoneyFormat())
}
