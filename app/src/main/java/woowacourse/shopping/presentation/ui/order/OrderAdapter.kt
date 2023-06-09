package woowacourse.shopping.presentation.ui.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemOrderBinding
import woowacourse.shopping.presentation.model.OrderUiModel

class OrderAdapter(
    private val onClick: (position: Int) -> Unit,
) : ListAdapter<OrderUiModel, OrderAdapter.OrderViewHolder>(OrderComparator) {

    class OrderViewHolder(
        val binding: ItemOrderBinding,
        private val onClick: (position: Int) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener { onClick(absoluteAdapterPosition) }
        }

        fun bind(data: OrderUiModel) {
            Glide.with(binding.imageOrderList).load(data.image).into(binding.imageOrderList)
            binding.textOrderListName.text = getOrderName(data)
            binding.textOrderListDateTime.text = data.dateTime
            binding.textOrderListTotalPrice.text = data.price.toString()
        }

        private fun getOrderName(data: OrderUiModel): String {
            return if (data.otherCount > 0) {
                binding.root.context.getString(
                    R.string.severalProducts,
                    data.name,
                    data.otherCount,
                )
            } else {
                data.name
            }
        }
    }

    object OrderComparator : DiffUtil.ItemCallback<OrderUiModel>() {
        override fun areItemsTheSame(oldItem: OrderUiModel, newItem: OrderUiModel): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: OrderUiModel, newItem: OrderUiModel): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemOrderBinding.inflate(layoutInflater, parent, false)
        return OrderViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
