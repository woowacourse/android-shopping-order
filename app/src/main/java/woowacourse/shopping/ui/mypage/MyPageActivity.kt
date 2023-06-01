package woowacourse.shopping.ui.mypage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import woowacourse.shopping.databinding.ActivityMyPageBinding
import woowacourse.shopping.utils.UserData

class MyPageActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMyPageBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setActionBar()

        initUserInfo()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setActionBar() {
        setSupportActionBar(binding.toolbarMyPage)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val navigationIcon = binding.toolbarMyPage.navigationIcon?.mutate()
        DrawableCompat.setTint(
            navigationIcon!!,
            ContextCompat.getColor(this, android.R.color.white),
        )
        binding.toolbarMyPage.navigationIcon = navigationIcon
    }

    private fun initUserInfo() {
        binding.tvUserId.text = UserData.id
    }

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, MyPageActivity::class.java)
            context.startActivity(intent)
        }
    }
}
