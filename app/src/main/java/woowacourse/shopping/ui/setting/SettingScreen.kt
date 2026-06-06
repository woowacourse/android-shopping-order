package woowacourse.shopping.ui.setting

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun SettingScreen(onNavigateBack: () -> Unit) {
    val viewModel: SettingViewModel = viewModel(factory = SettingViewModel.Factory)

    val notificationEnabled by viewModel.notificationEnabled.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(viewModel.events) {
        viewModel.events.collect { event ->
            when (event) {
                SettingEvent.OpenSystemSettings -> {
                    val intent =
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                    context.startActivity(intent)
                }
            }
        }
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .systemBarsPadding(),
    ) {
        PaymentTopAppBar(onNavigateBack = onNavigateBack)
        SettingScreenContent(
            notificationEnabled = notificationEnabled,
            onToggleNotification = {
                val isGranted =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS,
                        ) == PackageManager.PERMISSION_GRANTED
                    } else {
                        true
                    }
                viewModel.toggleNotification(isGranted)
            },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
        )
    }
}

@Composable
fun SettingScreenContent(
    notificationEnabled: Boolean,
    onToggleNotification: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(16.dp),
    ) {
        Text(
            text = "알림 설정",
            fontSize = 20.sp,
        )
        HorizontalDivider(thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.End),
        ) {
            Text(
                text = "미결제 알림",
                fontSize = 16.sp,
            )
            Switch(
                checked = notificationEnabled,
                onCheckedChange = { onToggleNotification() },
                colors =
                    SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xff04C09E),
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color(0xFFD0D0D0),
                        uncheckedBorderColor = Color(0xFFD0D0D0),
                    ),
            )
        }
    }
}

@Composable
private fun PaymentTopAppBar(onNavigateBack: () -> Unit) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(Color(0xff555555))
                .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "뒤로가기",
            modifier =
                Modifier
                    .padding(10.dp)
                    .clickable { onNavigateBack() },
            tint = Color.White,
        )
        Text(
            text = "결제하기",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.W500,
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun SettingScreenPreview() {
    SettingScreenContent(
        notificationEnabled = true,
        onToggleNotification = {},
        modifier = Modifier.fillMaxSize(),
    )
}
