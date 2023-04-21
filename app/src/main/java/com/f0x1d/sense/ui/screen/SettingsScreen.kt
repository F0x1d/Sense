package com.f0x1d.sense.ui.screen

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.f0x1d.sense.R
import com.f0x1d.sense.ui.widget.NavigationBackIcon
import com.f0x1d.sense.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val viewModel = hiltViewModel<SettingsViewModel>()

    val apiKey by viewModel.apiKey.observeAsState()
    val model by viewModel.model.observeAsState()

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    
    Column {
        LargeTopAppBar(
            title = { Text(text = stringResource(id = R.string.settings)) },
            navigationIcon = { NavigationBackIcon(navController = navController) },
            scrollBehavior = scrollBehavior
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                SettingsTextField(
                    value = apiKey ?: "",
                    onValueChange = { viewModel.updateFor(viewModel.apiKey, it) },
                    labelResource = R.string.api_key
                )

                SettingsTextField(
                    value = model ?: "",
                    onValueChange = { viewModel.updateFor(viewModel.model, it) },
                    labelResource = R.string.model
                )
            }
        }
    }
}

@Composable
private fun SettingsTextField(value: String, onValueChange: (String) -> Unit, @StringRes labelResource: Int) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = 10.dp,
                start = 10.dp,
                end = 10.dp
            ),
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = stringResource(id = labelResource)) },
        shape = RoundedCornerShape(12.dp)
    )
}