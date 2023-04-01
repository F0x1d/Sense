package com.f0x1d.sense.ui.widget

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.f0x1d.sense.R

@Composable
fun NavigationBackIcon(navController: NavController) {
    IconButton(onClick = { navController.popBackStack() }) {
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_back),
            contentDescription = null
        )
    }
}