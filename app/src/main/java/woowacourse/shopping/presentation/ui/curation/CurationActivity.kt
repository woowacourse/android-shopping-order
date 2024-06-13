package woowacourse.shopping.presentation.ui.curation

import android.content.Context
import android.content.Intent
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityCurationBinding
import woowacourse.shopping.presentation.base.BindingActivity
import woowacourse.shopping.presentation.ui.EventObserver
import woowacourse.shopping.presentation.ui.UiState
import woowacourse.shopping.presentation.ui.ViewModelFactory
import woowacourse.shopping.presentation.ui.cart.CartActivity
import woowacourse.shopping.presentation.ui.payment.PaymentActivity

class CurationActivity : BindingActivity<ActivityCurationBinding>() {
    override val layoutResourceId: Int
        get() = R.layout.activity_curation
    private val orderItemsId: List<Long> by lazy {
        intent.getIntegerArrayListExtra(EXTRA_CART_PRODUCT)?.map { it.toLong() } ?: emptyList()
    }
    private val onBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                CartActivity.createIntent(this@CurationActivity).apply {
                    putExtra(CartActivity.EXTRA_BACK_FROM_CURATION, true)
                    startActivity(this)
                    finish()
                }
            }
        }

    private val viewModel: CurationViewModel by viewModels { ViewModelFactory(orderItemsId) }

    private val curationAdapter: CurationAdapter by lazy { CurationAdapter(viewModel) }

    override fun initStartView() {
        title = "Curation"

        binding.rvCurations.adapter = curationAdapter
        binding.curationActionHandler = viewModel
        this.onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        viewModel.cartProducts.observe(this) {
            when (it) {
                is UiState.Loading -> {
                }

                is UiState.Success -> {
                    curationAdapter.submitList(it.data)
                }
            }
        }
        viewModel.orderProducts.observe(this) {
            when (it) {
                is UiState.Loading -> {
                }

                is UiState.Success -> {
                    binding.tvPrice.text =
                        getString(
                            R.string.won,
                            it.data.sumOf {
                                it.quantity * it.price
                            },
                        )

                    binding.tvOrderCount.text =
                        it.data.sumOf {
                            it.quantity
                        }.toString()
                }
            }
        }

        viewModel.eventHandler.observe(
            this,
            EventObserver {
                when (it) {
                    is CurationEvent.SuccessOrder -> {
                        PaymentActivity.createIntent(
                            this,
                            (viewModel.orderProducts.value as UiState.Success).data.map { it.cartId.toInt() },
                        )
                            .also {
                                startActivity(it)
                            }
                    }
                }
            },
        )
    }

    companion object {
        const val EXTRA_CART_PRODUCT = "cartProduct"

        fun createIntent(
            context: Context,
            orderItemsId: List<Int>,
        ): Intent {
            return Intent(context, CurationActivity::class.java).apply {
                putIntegerArrayListExtra(EXTRA_CART_PRODUCT, orderItemsId as ArrayList<Int>)
            }
        }
    }
}
