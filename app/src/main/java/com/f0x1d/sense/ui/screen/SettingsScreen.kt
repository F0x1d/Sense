package com.f0x1d.sense.ui.screen

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.f0x1d.sense.R
import com.f0x1d.sense.ui.widget.NavigationBackIcon
import com.f0x1d.sense.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val viewModel = hiltViewModel<SettingsViewModel>()

    val theme by viewModel.theme.collectAsStateWithLifecycle(initialValue = 0)
    val amoled by viewModel.amoled.collectAsStateWithLifecycle(initialValue = false)

    val apiKey by viewModel.apiKey
    val model by viewModel.model

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
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 10.dp)
            ) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    listOf(R.string.follow_system, R.string.light, R.string.dark).forEachIndexed { index, i -> 
                        FilterChip(
                            selected = theme == index, 
                            onClick = { viewModel.selectTheme(index) },
                            label = { Text(text = stringResource(id = i)) }
                        )
                    }
                }

                FilterChip(
                    selected = amoled,
                    onClick = { viewModel.selectAmoled(!amoled) },
                    label = { Text("AMOLED") }
                )

                Spacer(modifier = Modifier.size(5.dp))
                
                SettingsTextField(
                    value = apiKey,
                    onValueChange = { viewModel.updateFor(viewModel.apiKey, it) },
                    labelResource = R.string.api_key
                )

                Spacer(modifier = Modifier.size(10.dp))

                SettingsTextField(
                    value = model,
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
        modifier = Modifier.fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = stringResource(id = labelResource)) },
        shape = RoundedCornerShape(12.dp)
    )
}