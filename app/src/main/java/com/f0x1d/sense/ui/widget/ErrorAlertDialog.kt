package com.f0x1d.sense.ui.widget

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.f0x1d.sense.R
import com.f0x1d.sense.viewmodel.base.BaseViewModel

@Composable
fun ErrorAlertDialog(viewModel: BaseViewModel) {
    val error = viewModel.error ?: return

    AlertDialog(
        onDismissRequest = { viewModel.error = null },
        title = { Text(text = stringResource(id = R.string.error)) },
        text = { Text(text = error) },
        confirmButton = {
            TextButton(onClick = { viewModel.error = null }) {
                Text(text = stringResource(id = android.R.string.ok))
            }
        }
    )
}