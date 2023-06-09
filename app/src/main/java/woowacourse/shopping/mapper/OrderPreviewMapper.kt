package woowacourse.shopping.mapper

import com.example.domain.model.OrderPreview
import woowacourse.shopping.model.OrderPreviewUiModel

fun OrderPreview.toPresentation(): OrderPreviewUiModel {
    return OrderPreviewUiModel(
        orderId,
        mainName,
        mainImageUrl,
        extraProductCount,
        date,
        paymentAmount,
    )
}
