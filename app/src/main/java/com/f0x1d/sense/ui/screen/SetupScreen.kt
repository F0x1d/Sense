package com.f0x1d.sense.ui.screen

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.TypedValue
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.f0x1d.sense.R
import com.f0x1d.sense.viewmodel.SetupViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupScreen() {
    val viewModel = hiltViewModel<SetupViewModel>()

    var apiKey by rememberSaveable { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            TopAppBar(title = { Text(text = stringResource(id = R.string.setup)) })
            
            Column(modifier = Modifier.padding(horizontal = 15.dp)) {
                OutlinedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = 5.dp,
                            bottom = 15.dp
                        ),
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.secondaryContainer),
                ) {
                    Text(
                        modifier = Modifier.padding(15.dp),
                        text = stringResource(id = R.string.api_key_is_required_to_access_api),
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Text(
                    modifier = Modifier.padding(
                        start = 15.dp,
                        bottom = 3.dp
                    ),
                    text = stringResource(id = R.string.how_to_get_it),
                    style = MaterialTheme.typography.labelMedium,
                    color = getColor(color = android.R.attr.textColorSecondary)
                )
                
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.secondaryContainer),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 15.dp)
                    ) {
                        listOf(
                            buildFirstInstruction(),
                            AnnotatedString(stringResource(id = R.string.instruction_part_2)),
                            AnnotatedString(stringResource(id = R.string.instruction_part_3)),
                            AnnotatedString(stringResource(id = R.string.instruction_part_4)),
                            AnnotatedString(stringResource(id = R.string.instruction_part_5))
                        ).forEachIndexed { index, annotatedString ->
                            InstructionPart(index = index, instruction = annotatedString)
                        }
                    }
                }
            }

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                value = apiKey,
                onValueChange = { apiKey = it },
                label = { Text(text = stringResource(id = R.string.api_key)) },
                shape = RoundedCornerShape(12.dp)
            )
        }

        AnimatedVisibility(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .navigationBarsPadding(),
            visible = apiKey.isNotEmpty(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            FloatingActionButton(
                onClick = {
                    viewModel.saveApiKey(apiKey)
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_done),
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
fun InstructionPart(index: Int, instruction: AnnotatedString) {
    val context = LocalContext.current

    if (index != 0) {
        Divider()
    }

    Row(
        modifier = Modifier.padding(
            top = 10.dp,
            end = 15.dp,
            bottom = 10.dp
        )
    ) {
        Text(
            text = "${index + 1}.",
            color = getColor(color = android.R.attr.textColorSecondary)
        )

        Spacer(modifier = Modifier.size(7.dp))

        ClickableText(
            text = instruction,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = LocalContentColor.current
            ),
            onClick = { offset ->
                instruction.getStringAnnotations(tag = "openai", offset, offset).firstOrNull()
                    ?.let {
                        context.startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse(it.item)))
                    }
            }
        )
    }
}

@Composable
private fun buildFirstInstruction() = buildAnnotatedString {
    append(stringResource(id = R.string.instruction_part_1_1))
    append(" ")

    pushStringAnnotation("openai", annotation = "https://beta.openai.com/account")
    withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
        append(stringResource(id = R.string.instruction_part_1_2))
    }
    pop()
}

@Composable
fun getColor(color: Int) = colorResource(LocalContext.current.getColorFromAttrs(color).resourceId)
private fun Context.getColorFromAttrs(attr: Int) = TypedValue().apply {
    theme.resolveAttribute(attr, this, true)
}