package com.billcorea.sudabot0222.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.icons.outlined.Title
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.billcorea.sudabot0222.R
import com.billcorea.sudabot0222.retrofit.DataViewModel
import com.billcorea.sudabot0222.ui.theme.softBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomScreen (
    doAppendChat:(sendText : String) -> Unit,
) {
    var sendText by remember { mutableStateOf( "") }
    Row( modifier = Modifier
        .fillMaxWidth()
        .padding(3.dp) ) {
        OutlinedTextField(
            value = sendText,
            onValueChange = { sendText = it },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp)
                .onKeyEvent {
                        if (it.nativeKeyEvent.keyCode == android.view.KeyEvent.KEYCODE_ENTER) {
                             doAppendChat(sendText)
                             sendText = ""
                             true
                        }
                    false
                },
            trailingIcon = {
                IconButton(onClick = {
                    doAppendChat(sendText)
                    sendText = ""
                }) {
                    Icon(imageVector = Icons.Outlined.Send, contentDescription = "Send", tint = softBlue)
                }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopScreen(
    dataViewModel: DataViewModel,
    doClearChatData:() -> Unit,
    doInformation:() -> Unit,
) {
    Row( modifier = Modifier
        .fillMaxWidth()
        .padding(3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = dataViewModel.infoText.value,
            onValueChange = { dataViewModel.infoText.value = it },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp),
            leadingIcon = {
                Row( modifier = Modifier
                    .padding(3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Icon(painter = painterResource(id = R.drawable.ic_sudabot_icon), contentDescription = "App Icon", tint = softBlue)
                    IconButton(onClick = { doInformation() }) {
                        Icon(imageVector = Icons.Outlined.Info, contentDescription = "Information", tint = softBlue)
                    }
                    Icon(imageVector = Icons.Outlined.Title, contentDescription = "Title", tint = softBlue)
                }
            },
            trailingIcon = {
                IconButton(onClick = { doClearChatData() }) {
                    Icon(imageVector = Icons.Outlined.Clear, contentDescription = "Clear", tint = softBlue)
                }
            }
        )
    }
}