package woowacourse.shopping.view.order.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import woowacourse.shopping.databinding.ItemOrderTitleBinding
import woowacourse.shopping.view.core.base.BaseViewHolder

class OrderTitleViewHolder(
    parent: ViewGroup,
) : BaseViewHolder<ItemOrderTitleBinding>(
        ItemOrderTitleBinding.inflate(LayoutInflater.from(parent.context), parent, false),
    )
