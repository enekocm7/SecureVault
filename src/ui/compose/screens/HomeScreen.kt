package ui.compose.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.enekocm.securevault.R
import domain.model.PasswordDto
import ui.compose.theme.GrisClaro
import ui.compose.theme.LightRed
import ui.compose.theme.Red
import ui.compose.theme.SecureVaultTheme
import ui.compose.theme.White
import ui.compose.theme.Black

// 1:1 of main_screen.xml + password_item.xml
@Composable
fun HomeScreen(
    passwords: List<PasswordDto>,
    isLoading: Boolean,
    isSearchMode: Boolean,
    searchQuery: String,
    profilePhotoUrl: String?,
    onSearchToggle: () -> Unit,
    onSearchChange: (String) -> Unit,
    onSettings: () -> Unit,
    onAdd: () -> Unit,
    onCopy: (PasswordDto) -> Unit,
    onItemClick: (PasswordDto) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightRed)
            .padding(top = 30.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top bar: CardView 70dp, corner 15, red bg
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .padding(10.dp),
                shape = RoundedCornerShape(15.dp),
                colors = CardDefaults.cardColors(containerColor = Red),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 15.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!isSearchMode) {
                        if (profilePhotoUrl != null) {
                            AsyncImage(
                                model = profilePhotoUrl,
                                contentDescription = stringResource(R.string.setting),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .clickable(onClick = onSettings)
                            )
                        } else {
                            IconButton(onClick = onSettings, modifier = Modifier.size(36.dp)) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_settings),
                                    contentDescription = stringResource(R.string.setting),
                                    tint = White
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = stringResource(R.string.passwords),
                            color = White, fontSize = 20.sp, fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = onSearchChange,
                            placeholder = { Text(stringResource(R.string.search_bar_text), color = White.copy(alpha = 0.8f)) },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = White,
                                unfocusedBorderColor = White,
                                cursorColor = White,
                                focusedTextColor = White,
                                unfocusedTextColor = White
                            )
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                    }
                    IconButton(onClick = onSearchToggle, modifier = Modifier.size(36.dp)) {
                        Icon(
                            painter = painterResource(if (isSearchMode) R.drawable.ic_close else R.drawable.ic_search),
                            contentDescription = stringResource(R.string.search),
                            tint = White
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Red,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else if (passwords.isEmpty()) {
                    Text(
                        text = stringResource(R.string.no_passwords_found),
                        color = Black, fontSize = 28.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center).offset(y = (-60).dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 100.dp)
                    ) {
                        items(passwords, key = { it.name }) { item ->
                            PasswordRow(
                                item = item,
                                onCopy = { onCopy(item) },
                                onClick = { onItemClick(item) }
                            )
                        }
                    }
                }
            }
        }
        // FAB: 65dp red circle bottom-end
        androidx.compose.material3.FloatingActionButton(
            onClick = onAdd,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 25.dp, bottom = 35.dp)
                .size(65.dp),
            containerColor = Red,
            contentColor = White,
            shape = CircleShape
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_add),
                contentDescription = stringResource(R.string.add_password),
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun PasswordRow(item: PasswordDto, onCopy: () -> Unit, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = LightRed),
        elevation = CardDefaults.cardElevation(4.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Black)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Black)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = item.username, fontSize = 14.sp, color = GrisClaro)
            }
            IconButton(onClick = onCopy, modifier = Modifier.size(48.dp)) {
                Icon(
                    painter = painterResource(R.drawable.ic_content_copy),
                    contentDescription = stringResource(R.string.copy_password),
                    tint = Black
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    SecureVaultTheme(darkTheme = false) {
        HomeScreen(
            passwords = listOf(
                PasswordDto("Amazon", "https://amazon.com", "usuario6@email.com", "x"),
                PasswordDto("Android Studio", "", "android165_studio", "x")
            ),
            isLoading = false, isSearchMode = false, searchQuery = "", profilePhotoUrl = null,
            onSearchToggle = {}, onSearchChange = {}, onSettings = {}, onAdd = {},
            onCopy = {}, onItemClick = {}
        )
    }
}
