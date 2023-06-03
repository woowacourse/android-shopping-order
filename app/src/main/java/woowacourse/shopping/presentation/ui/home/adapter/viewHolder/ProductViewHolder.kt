package woowacourse.shopping.presentation.ui.home.adapter.viewHolder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemProductBinding
import woowacourse.shopping.domain.model.Operator
import woowacourse.shopping.presentation.model.ProductItem
import woowacourse.shopping.presentation.ui.home.adapter.ProductClickListener

class ProductViewHolder(
    private val binding: ItemProductBinding,
    productClickListener: ProductClickListener,
    quantityChangeListener: (position: Int, op: Operator) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.listener = productClickListener
        binding.customItemProductCounter.setMinValue(0)
        binding.buttonItemProductInsertProduct.setOnClickListener {
            quantityChangeListener(absoluteAdapterPosition, Operator.INCREASE)
        }
        binding.customItemProductCounter.setMinValue(0)
        binding.customItemProductCounter.setIncreaseClickListener {
            quantityChangeListener(absoluteAdapterPosition, Operator.INCREASE)
        }
        binding.customItemProductCounter.setDecreaseClickListener {
            setVisibility(binding.customItemProductCounter.currentQuantity)
            quantityChangeListener(absoluteAdapterPosition, Operator.DECREASE)
        }
    }

    private fun setVisibility(quantity: Int) {
        binding.buttonItemProductInsertProduct.visibility =
            if (quantity == 0) View.VISIBLE else View.GONE
        binding.customItemProductCounter.visibility =
            if (quantity == 0) View.GONE else View.VISIBLE
    }

    fun bind(data: ProductItem) {
        binding.product = data
        binding.customItemProductCounter.setQuantityText(data.quantity)
        setVisibility(data.quantity)
    }

    companion object {
        fun getView(parent: ViewGroup): ItemProductBinding {
            val layoutInflater = LayoutInflater.from(parent.context)
            return ItemProductBinding.inflate(layoutInflater, parent, false)
        }
    }
}
