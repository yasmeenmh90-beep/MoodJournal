package com.example.moodjournal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
val DarkBgTop = Color(0xFF0D0D1A)
val DarkBgMid = Color(0xFF1A1035)
val DarkBgBottom = Color(0xFF0D1F3C)
val DarkCard = Color(0xFF1E1E3A)
val DarkCard2 = Color(0xFF162040)
val LightBgTop = Color(0xFFF0EFFF)
val LightBgMid = Color(0xFFE8E6FF)
val LightBgBottom = Color(0xFFE0F0FF)
val LightCard = Color(0xFFFFFFFF)
val LightCard2 = Color(0xFFF5F5FF)
val Purple = Color(0xFF7C6FFF)
val PurpleLight = Color(0xFFAA9FFF)

val quotes = listOf(
    "Every day is a fresh start 🌅",
    "Your feelings are valid 💜",
    "Small steps lead to big changes 🌱",
    "Be kind to yourself today 🤍",
    "You are stronger than you think 💪"
)

data class MoodEntry(
    val emoji: String,
    val moodName: String,
    val note: String,
    val date: String,
    val dayKey: String,
    val color: Color,
    val isFavorite: Boolean = false
)

data class CustomMood(
    val emoji: String,
    val name: String,
    val color: Color
)

val defaultMoods = listOf(
    CustomMood("😄", "Amazing", Color(0xFFFFD93D)),
    CustomMood("😊", "Happy", Color(0xFF6BCB77)),
    CustomMood("😌", "Calm", Color(0xFF4ECDC4)),
    CustomMood("😐", "Okay", Color(0xFF4D96FF)),
    CustomMood("😔", "Sad", Color(0xFFB8B8FF)),
    CustomMood("😢", "Very Sad", Color(0xFF9B59B6)),
    CustomMood("😡", "Angry", Color(0xFFFF6B6B)),
    CustomMood("😰", "Anxious", Color(0xFFFF9F43))
)

val extraEmojis = listOf(
    "🥳","😎","🤩","😴","🤔","😤","🥺","😇",
    "🤗","😑","😬","🙃","🥰","😏","😶","🫠"
)

val moodColors = listOf(
    Color(0xFFFF6B6B), Color(0xFFFFD93D), Color(0xFF6BCB77),
    Color(0xFF4D96FF), Color(0xFF9B59B6), Color(0xFFFF9F43),
    Color(0xFF4ECDC4), Color(0xFFB8B8FF)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MoodJournalApp() }
    }
}

@Composable
fun MoodJournalApp() {
    var screen by remember { mutableStateOf("pin_setup") }
    var isDark by remember { mutableStateOf(true) }
    var entries by remember { mutableStateOf(listOf<MoodEntry>()) }
    var selectedMood by remember { mutableStateOf(defaultMoods[0]) }
    var customMoods by remember { mutableStateOf(defaultMoods) }
    var savedPin by remember { mutableStateOf("") }
    var isLocked by remember { mutableStateOf(true) }

    val bgColors = if (isDark)
        listOf(DarkBgTop, DarkBgMid, DarkBgBottom)
    else listOf(LightBgTop, LightBgMid, LightBgBottom)

    val cardBg = if (isDark) DarkCard else LightCard
    val cardBg2 = if (isDark) DarkCard2 else LightCard2
    val textColor = if (isDark) Color.White else Color(0xFF1A1A2E)

    Box(
        modifier = Modifier.fillMaxSize()
            .background(Brush.verticalGradient(bgColors))
    ) {
        when {
            screen == "pin_setup" -> PinSetupScreen(
                isDark = isDark,
                textColor = textColor,
                cardBg = cardBg,
                onPinSet = { pin ->
                    savedPin = pin
                    isLocked = false
                    screen = "home"
                }
            )
            isLocked -> PinLockScreen(
                isDark = isDark,
                textColor = textColor,
                cardBg = cardBg,
                correctPin = savedPin,
                onUnlocked = { isLocked = false; screen = "home" }
            )
            screen == "home" -> HomeScreen(
                entries = entries,
                isDark = isDark,
                cardBg = cardBg,
                cardBg2 = cardBg2,
                textColor = textColor,
                onToggleTheme = { isDark = !isDark },
                onAddMood = { screen = "add" },
                onHistory = { screen = "history" },
                onFavorites = { screen = "favorites" },
                onChart = { screen = "chart" },
                onCustomMoods = { screen = "custom" },
                onLock = { isLocked = true }
            )
            screen == "add" -> AddMoodScreen(
                selectedMood = selectedMood,
                allMoods = customMoods,
                isDark = isDark,
                cardBg = cardBg,
                textColor = textColor,
                onMoodSelected = { selectedMood = it },
                onSave = { note ->
                    val today = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())
                    val dayKey = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    entries = listOf(
                        MoodEntry(
                            emoji = selectedMood.emoji,
                            moodName = selectedMood.name,
                            note = note,
                            date = today,
                            dayKey = dayKey,
                            color = selectedMood.color
                        )
                    ) + entries
                    screen = "home"
                },
                onBack = { screen = "home" }
            )
            screen == "history" -> HistoryScreen(
                entries = entries,
                isDark = isDark,
                cardBg = cardBg,
                textColor = textColor,
                onToggleFav = { entry ->
                    entries = entries.map {
                        if (it == entry) it.copy(isFavorite = !it.isFavorite) else it
                    }
                },
                onBack = { screen = "home" }
            )
            screen == "favorites" -> FavoritesScreen(
                entries = entries.filter { it.isFavorite },
                isDark = isDark,
                cardBg = cardBg,
                textColor = textColor,
                onBack = { screen = "home" }
            )
            screen == "chart" -> ChartScreen(
                entries = entries,
                isDark = isDark,
                cardBg = cardBg,
                textColor = textColor,
                onBack = { screen = "home" }
            )
            screen == "custom" -> CustomMoodsScreen(
                customMoods = customMoods,
                isDark = isDark,
                cardBg = cardBg,
                textColor = textColor,
                onSave = { newMoods -> customMoods = newMoods; screen = "home" },
                onBack = { screen = "home" }
            )
        }
    }
}

// ─── PIN Setup Screen ───
@Composable
fun PinSetupScreen(
    isDark: Boolean,
    textColor: Color,
    cardBg: Color,
    onPinSet: (String) -> Unit
) {
    var pin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var step by remember { mutableStateOf(1) }
    var error by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🔐", fontSize = 64.sp)
        Spacer(Modifier.height(16.dp))
        Text(
            if (step == 1) "Create your PIN" else "Confirm your PIN",
            fontSize = 26.sp, fontWeight = FontWeight.Bold, color = textColor,
            textAlign = TextAlign.Center
        )
        Text(
            if (step == 1) "Set a 4-digit PIN to secure your journal"
            else "Enter the PIN again to confirm",
            fontSize = 14.sp, color = textColor.copy(0.5f),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(32.dp))

        // PIN dots
        val currentPin = if (step == 1) pin else confirmPin
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            repeat(4) { i ->
                Box(
                    modifier = Modifier.size(20.dp).clip(CircleShape)
                        .background(
                            if (i < currentPin.length) Purple
                            else textColor.copy(0.2f)
                        )
                )
            }
        }

        if (error.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))
            Text(error, color = Color(0xFFFF6B6B), fontSize = 14.sp)
        }

        Spacer(Modifier.height(32.dp))

        // Number pad
        PinPad(
            textColor = textColor,
            cardBg = cardBg,
            onDigit = { digit ->
                error = ""
                if (step == 1 && pin.length < 4) {
                    pin += digit
                    if (pin.length == 4) step = 2
                } else if (step == 2 && confirmPin.length < 4) {
                    confirmPin += digit
                    if (confirmPin.length == 4) {
                        if (confirmPin == pin) onPinSet(pin)
                        else {
                            error = "PINs don't match! Try again"
                            confirmPin = ""
                            pin = ""
                            step = 1
                        }
                    }
                }
            },
            onDelete = {
                error = ""
                if (step == 1 && pin.isNotEmpty()) pin = pin.dropLast(1)
                else if (step == 2 && confirmPin.isNotEmpty()) confirmPin = confirmPin.dropLast(1)
            }
        )

        Spacer(Modifier.height(16.dp))
        TextButton(onClick = { onPinSet("") }) {
            Text("Skip PIN setup", color = textColor.copy(0.4f), fontSize = 14.sp)
        }
    }
}

// ─── PIN Lock Screen ───
@Composable
fun PinLockScreen(
    isDark: Boolean,
    textColor: Color,
    cardBg: Color,
    correctPin: String,
    onUnlocked: () -> Unit
) {
    var pin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var shake by remember { mutableStateOf(false) }

    LaunchedEffect(shake) {
        if (shake) {
            kotlinx.coroutines.delay(500)
            shake = false
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🔒", fontSize = 64.sp)
        Spacer(Modifier.height(16.dp))
        Text("Welcome Back!", fontSize = 26.sp,
            fontWeight = FontWeight.Bold, color = textColor)
        Text("Enter your PIN to unlock", fontSize = 14.sp,
            color = textColor.copy(0.5f))
        Spacer(Modifier.height(32.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            repeat(4) { i ->
                Box(
                    modifier = Modifier.size(20.dp).clip(CircleShape)
                        .background(
                            if (i < pin.length) Purple
                            else textColor.copy(0.2f)
                        )
                )
            }
        }

        if (error.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))
            Text(error, color = Color(0xFFFF6B6B), fontSize = 14.sp)
        }

        Spacer(Modifier.height(32.dp))

        PinPad(
            textColor = textColor,
            cardBg = cardBg,
            onDigit = { digit ->
                error = ""
                if (pin.length < 4) {
                    pin += digit
                    if (pin.length == 4) {
                        if (pin == correctPin || correctPin.isEmpty()) {
                            onUnlocked()
                        } else {
                            error = "Wrong PIN! Try again ❌"
                            shake = true
                            pin = ""
                        }
                    }
                }
            },
            onDelete = {
                error = ""
                if (pin.isNotEmpty()) pin = pin.dropLast(1)
            }
        )
    }
}

// ─── PIN Pad ───
@Composable
fun PinPad(
    textColor: Color,
    cardBg: Color,
    onDigit: (String) -> Unit,
    onDelete: () -> Unit
) {
    val keys = listOf("1","2","3","4","5","6","7","8","9","","0","⌫")
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        keys.chunked(3).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { key ->
                    if (key.isEmpty()) {
                        Box(modifier = Modifier.size(80.dp))
                    } else {
                        Box(
                            modifier = Modifier.size(80.dp).clip(CircleShape)
                                .background(cardBg.copy(0.8f))
                                .clickable {
                                    if (key == "⌫") onDelete() else onDigit(key)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(key, fontSize = 24.sp,
                                fontWeight = FontWeight.Bold, color = textColor)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CustomMoodsScreen(
    customMoods: List<CustomMood>,
    isDark: Boolean,
    cardBg: Color,
    textColor: Color,
    onSave: (List<CustomMood>) -> Unit,
    onBack: () -> Unit
) {
    var moods by remember { mutableStateOf(customMoods) }
    var showAddScreen by remember { mutableStateOf(false) }
    var newEmoji by remember { mutableStateOf("🥳") }
    var newName by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(moodColors[0]) }
    val scrollState = rememberScrollState()

    if (showAddScreen) {
        val addScroll = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(if (isDark) DarkBgTop else LightBgTop)
                .padding(horizontal = 24.dp)
                .verticalScroll(addScroll),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(52.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(cardBg.copy(0.6f))
                        .clickable { showAddScreen = false; newName = "" }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) { Text("← Back", color = textColor.copy(0.8f), fontSize = 14.sp) }
            }

            Spacer(Modifier.height(16.dp))
            Text("Add New Mood", fontSize = 24.sp,
                fontWeight = FontWeight.Bold, color = textColor)
            Spacer(Modifier.height(20.dp))

            Box(
                modifier = Modifier.size(90.dp).clip(CircleShape)
                    .background(selectedColor.copy(0.2f)),
                contentAlignment = Alignment.Center
            ) { Text(newEmoji, fontSize = 48.sp) }

            Spacer(Modifier.height(20.dp))

            Text("Pick an emoji", fontSize = 15.sp,
                fontWeight = FontWeight.Bold, color = textColor)
            Spacer(Modifier.height(10.dp))

            extraEmojis.chunked(4).forEach { row ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    row.forEach { emoji ->
                        Box(
                            modifier = Modifier.size(54.dp).clip(CircleShape)
                                .background(
                                    if (emoji == newEmoji) Purple.copy(0.3f)
                                    else textColor.copy(0.08f)
                                )
                                .clickable { newEmoji = emoji },
                            contentAlignment = Alignment.Center
                        ) { Text(emoji, fontSize = 24.sp) }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Text("Mood name", fontSize = 15.sp,
                fontWeight = FontWeight.Bold, color = textColor)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = newName,
                onValueChange = { newName = it },
                placeholder = {
                    Text("e.g. Excited", color = textColor.copy(0.3f))
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor,
                    focusedBorderColor = Purple,
                    unfocusedBorderColor = textColor.copy(0.2f),
                    cursorColor = Purple
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(Modifier.height(20.dp))

            Text("Pick a color", fontSize = 15.sp,
                fontWeight = FontWeight.Bold, color = textColor)
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                moodColors.forEach { color ->
                    Box(
                        modifier = Modifier.size(38.dp).clip(CircleShape)
                            .background(color)
                            .clickable { selectedColor = color },
                        contentAlignment = Alignment.Center
                    ) {
                        if (color == selectedColor)
                            Text("✓", color = Color.White,
                                fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    if (newName.isNotEmpty()) {
                        moods = moods + CustomMood(newEmoji, newName, selectedColor)
                        showAddScreen = false
                        newName = ""
                        newEmoji = "🥳"
                        selectedColor = moodColors[0]
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Purple),
                shape = RoundedCornerShape(18.dp),
                enabled = newName.isNotEmpty()
            ) {
                Text("Add Mood ✓", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(32.dp))
        }

    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(if (isDark) DarkBgTop else LightBgTop)
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopBar("", isDark, textColor, cardBg, onBack = onBack)
            Text("🎨 Custom Moods", fontSize = 26.sp,
                fontWeight = FontWeight.Bold, color = textColor)
            Text("Add your own moods!", fontSize = 13.sp,
                color = textColor.copy(0.4f))
            Spacer(Modifier.height(16.dp))

            // LazyColumn hataya — forEach use kiya
            moods.forEach { mood ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(mood.color.copy(0.15f))
                        .padding(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(mood.emoji, fontSize = 28.sp)
                        Spacer(Modifier.width(12.dp))
                        Text(mood.name, fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = mood.color,
                            modifier = Modifier.weight(1f))
                        if (moods.indexOf(mood) >= 8) {
                            Text("🗑️", fontSize = 20.sp,
                                modifier = Modifier.clickable {
                                    moods = moods.filter { it != mood }
                                })
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { showAddScreen = true },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Purple),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("+ Add New Mood", fontSize = 16.sp,
                    fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(10.dp))

            Button(
                onClick = { onSave(moods) },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6BCB77)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Save Changes ✓", fontSize = 16.sp,
                    fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

fun calculateStreak(entries: List<MoodEntry>): Int {
    if (entries.isEmpty()) return 0
    val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val today = fmt.format(Date())
    val days = entries.map { it.dayKey }.distinct().sortedDescending()
    if (days.first() != today) return 0
    var streak = 1
    val cal = Calendar.getInstance()
    for (i in 1 until days.size) {
        cal.time = fmt.parse(days[i - 1])!!
        cal.add(Calendar.DAY_OF_YEAR, -1)
        if (fmt.format(cal.time) == days[i]) streak++ else break
    }
    return streak
}

@Composable
fun TopBar(
    title: String,
    isDark: Boolean,
    textColor: Color,
    cardBg: Color,
    showThemeToggle: Boolean = false,
    onToggleTheme: (() -> Unit)? = null,
    onLock: (() -> Unit)? = null,
    onBack: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 52.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBack != null) {
            Box(
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
                    .background(cardBg.copy(0.6f))
                    .clickable { onBack() }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) { Text("← Back", color = textColor.copy(0.8f), fontSize = 14.sp) }
        } else {
            Text(title, fontSize = 26.sp,
                fontWeight = FontWeight.Bold, color = textColor)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (onLock != null) {
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFFF6B6B).copy(0.15f))
                        .clickable { onLock() }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) { Text("🔒 Lock", fontSize = 13.sp, color = Color(0xFFFF6B6B)) }
            }
            if (showThemeToggle && onToggleTheme != null) {
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(20.dp))
                        .background(Purple.copy(0.2f))
                        .clickable { onToggleTheme() }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(if (isDark) "☀️ Light" else "🌙 Dark",
                        fontSize = 13.sp, color = Purple)
                }
            }
        }
    }
}

@Composable
fun MoodCard(
    entry: MoodEntry,
    cardBg: Color,
    textColor: Color,
    showFavButton: Boolean = false,
    isFav: Boolean = false,
    onFavToggle: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(listOf(entry.color.copy(0.15f), cardBg)))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(52.dp).clip(CircleShape)
                    .background(entry.color.copy(0.2f)),
                contentAlignment = Alignment.Center
            ) { Text(entry.emoji, fontSize = 28.sp) }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(entry.moodName, fontSize = 17.sp,
                    fontWeight = FontWeight.Bold, color = entry.color)
                if (entry.note.isNotEmpty()) {
                    Spacer(Modifier.height(2.dp))
                    Text("\"${entry.note}\"", fontSize = 13.sp,
                        color = textColor.copy(0.65f), maxLines = 2)
                }
                Spacer(Modifier.height(4.dp))
                Text(entry.date, fontSize = 11.sp, color = textColor.copy(0.35f))
            }
            if (showFavButton && onFavToggle != null) {
                Text(if (isFav) "❤️" else "🤍", fontSize = 22.sp,
                    modifier = Modifier.clickable { onFavToggle() })
            }
        }
    }
}

@Composable
fun HomeScreen(
    entries: List<MoodEntry>,
    isDark: Boolean,
    cardBg: Color,
    cardBg2: Color,
    textColor: Color,
    onToggleTheme: () -> Unit,
    onAddMood: () -> Unit,
    onHistory: () -> Unit,
    onFavorites: () -> Unit,
    onChart: () -> Unit,
    onCustomMoods: () -> Unit,
    onLock: () -> Unit
) {
    val today = SimpleDateFormat("EEEE, dd MMM", Locale.getDefault()).format(Date())
    val lastMood = entries.firstOrNull()
    val streak = calculateStreak(entries)
    val randomQuote = remember { quotes.random() }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            tween(1200, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "scale"
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            TopBar(
                title = "✨ Mood Journal",
                isDark = isDark,
                textColor = textColor,
                cardBg = cardBg,
                showThemeToggle = true,
                onToggleTheme = onToggleTheme,
                onLock = onLock
            )

            Text(today, fontSize = 14.sp, color = textColor.copy(0.5f))
            Spacer(Modifier.height(10.dp))

            Box(
                modifier = Modifier.clip(RoundedCornerShape(20.dp))
                    .background(Purple.copy(0.15f))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(randomQuote, fontSize = 13.sp,
                    color = PurpleLight, textAlign = TextAlign.Center)
            }

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier.weight(1f).clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFFF6B6B).copy(0.12f)).padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🔥", fontSize = 22.sp)
                        Text("$streak", fontSize = 22.sp,
                            fontWeight = FontWeight.Bold, color = Color(0xFFFF6B6B))
                        Text("Streak", fontSize = 11.sp, color = textColor.copy(0.5f))
                    }
                }
                Box(
                    modifier = Modifier.weight(1f).clip(RoundedCornerShape(20.dp))
                        .background(Purple.copy(0.12f)).padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📝", fontSize = 22.sp)
                        Text("${entries.size}", fontSize = 22.sp,
                            fontWeight = FontWeight.Bold, color = Purple)
                        Text("Total", fontSize = 11.sp, color = textColor.copy(0.5f))
                    }
                }
                if (entries.isNotEmpty()) {
                    val topMood = entries.groupBy { it.moodName }
                        .maxByOrNull { it.value.size }?.value?.first()
                    Box(
                        modifier = Modifier.weight(1f).clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF6BCB77).copy(0.12f)).padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(topMood?.emoji ?: "😊", fontSize = 22.sp)
                            Text(topMood?.moodName ?: "", fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6BCB77),
                                textAlign = TextAlign.Center)
                            Text("Top", fontSize = 11.sp, color = textColor.copy(0.5f))
                        }
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            Box(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(28.dp))
                    .background(Brush.linearGradient(listOf(cardBg, cardBg2)))
                    .padding(28.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (lastMood != null) {
                        Text(lastMood.emoji, fontSize = 72.sp,
                            modifier = Modifier.scale(scale))
                        Spacer(Modifier.height(10.dp))
                        Text("Feeling ${lastMood.moodName}", fontSize = 22.sp,
                            fontWeight = FontWeight.Bold, color = lastMood.color)
                        Text("Last: ${lastMood.date}", fontSize = 12.sp,
                            color = textColor.copy(0.4f))
                        if (lastMood.note.isNotEmpty()) {
                            Spacer(Modifier.height(10.dp))
                            Box(
                                modifier = Modifier.clip(RoundedCornerShape(12.dp))
                                    .background(lastMood.color.copy(0.15f))
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text("\"${lastMood.note}\"", fontSize = 13.sp,
                                    color = lastMood.color.copy(0.9f),
                                    textAlign = TextAlign.Center)
                            }
                        }
                    } else {
                        Text("🌟", fontSize = 72.sp, modifier = Modifier.scale(scale))
                        Spacer(Modifier.height(10.dp))
                        Text("How are you feeling?", fontSize = 22.sp,
                            fontWeight = FontWeight.Bold, color = textColor)
                        Text("Log your first mood today!", fontSize = 14.sp,
                            color = textColor.copy(0.5f))
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            Button(
                onClick = onAddMood,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Purple),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text("+ Log Today's Mood", fontSize = 17.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NavChip("📖 History", cardBg, textColor, Modifier.weight(1f)) { onHistory() }
                NavChip("❤️ Faves", cardBg, textColor, Modifier.weight(1f)) { onFavorites() }
                NavChip("📊 Chart", cardBg, textColor, Modifier.weight(1f)) { onChart() }
                NavChip("🎨 Moods", cardBg, textColor, Modifier.weight(1f)) { onCustomMoods() }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
fun NavChip(
    label: String, cardBg: Color, textColor: Color,
    modifier: Modifier = Modifier, onClick: () -> Unit
) {
    Box(
        modifier = modifier.clip(RoundedCornerShape(14.dp))
            .background(cardBg.copy(0.7f))
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(label, fontSize = 11.sp,
            color = textColor.copy(0.8f), fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center)
    }
}

@Composable
fun AddMoodScreen(
    selectedMood: CustomMood,
    allMoods: List<CustomMood>,
    isDark: Boolean,
    cardBg: Color,
    textColor: Color,
    onMoodSelected: (CustomMood) -> Unit,
    onSave: (String) -> Unit,
    onBack: () -> Unit
) {
    var note by remember { mutableStateOf("") }
    val bounce by rememberInfiniteTransition(label = "b").animateFloat(
        1f, 1.1f,
        infiniteRepeatable(tween(800, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "b"
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            TopBar("", isDark, textColor, cardBg, onBack = onBack)
            Text("How are you feeling?", fontSize = 26.sp,
                fontWeight = FontWeight.Bold, color = textColor,
                textAlign = TextAlign.Center)
            Text("Tap an emoji to select", fontSize = 14.sp,
                color = textColor.copy(0.4f))
            Spacer(Modifier.height(24.dp))

            Box(
                modifier = Modifier.size(110.dp).clip(CircleShape)
                    .background(selectedMood.color.copy(0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(selectedMood.emoji, fontSize = 56.sp,
                    modifier = Modifier.scale(bounce))
            }
            Spacer(Modifier.height(8.dp))
            Text(selectedMood.name, fontSize = 20.sp,
                fontWeight = FontWeight.Bold, color = selectedMood.color)
            Spacer(Modifier.height(24.dp))

            allMoods.chunked(4).forEach { row ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(vertical = 5.dp)
                ) {
                    row.forEach { mood ->
                        val isSelected = mood == selectedMood
                        Box(
                            modifier = Modifier.size(64.dp).clip(CircleShape)
                                .background(
                                    if (isSelected) mood.color.copy(0.35f)
                                    else cardBg.copy(0.6f)
                                )
                                .clickable { onMoodSelected(mood) },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(mood.emoji, fontSize = 24.sp)
                                if (isSelected)
                                    Text(mood.name, fontSize = 7.sp,
                                        color = mood.color, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            OutlinedTextField(
                value = note, onValueChange = { note = it },
                placeholder = {
                    Text("Write about your day... (optional)",
                        color = textColor.copy(0.3f), fontSize = 14.sp)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor,
                    focusedBorderColor = Purple,
                    unfocusedBorderColor = textColor.copy(0.15f),
                    cursorColor = Purple
                ),
                shape = RoundedCornerShape(16.dp),
                maxLines = 3, minLines = 2
            )
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = { onSave(note) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Purple),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text("Save Mood ✓", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
fun HistoryScreen(
    entries: List<MoodEntry>,
    isDark: Boolean,
    cardBg: Color,
    textColor: Color,
    onToggleFav: (MoodEntry) -> Unit,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
        TopBar("", isDark, textColor, cardBg, onBack = onBack)
        Text("Mood History", fontSize = 26.sp,
            fontWeight = FontWeight.Bold, color = textColor)
        Text("${entries.size} entries", fontSize = 13.sp, color = textColor.copy(0.4f))
        Spacer(Modifier.height(16.dp))
        if (entries.isEmpty()) {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📭", fontSize = 56.sp)
                    Text("No entries yet!", fontSize = 18.sp, color = textColor.copy(0.5f))
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(entries) { entry ->
                    MoodCard(
                        entry = entry, cardBg = cardBg, textColor = textColor,
                        showFavButton = true, isFav = entry.isFavorite,
                        onFavToggle = { onToggleFav(entry) }
                    )
                }
                item { Spacer(Modifier.height(24.dp)) }
            }
        }
    }
}

@Composable
fun FavoritesScreen(
    entries: List<MoodEntry>,
    isDark: Boolean,
    cardBg: Color,
    textColor: Color,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
        TopBar("", isDark, textColor, cardBg, onBack = onBack)
        Text("❤️ Favorites", fontSize = 26.sp,
            fontWeight = FontWeight.Bold, color = textColor)
        Text("${entries.size} saved", fontSize = 13.sp, color = textColor.copy(0.4f))
        Spacer(Modifier.height(16.dp))
        if (entries.isEmpty()) {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🤍", fontSize = 56.sp)
                    Text("No favorites yet!", fontSize = 18.sp, color = textColor.copy(0.5f))
                    Text("Tap ❤️ in History!", fontSize = 14.sp, color = textColor.copy(0.3f))
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(entries) { entry ->
                    MoodCard(entry = entry, cardBg = cardBg, textColor = textColor)
                }
                item { Spacer(Modifier.height(24.dp)) }
            }
        }
    }
}

@Composable
fun ChartScreen(
    entries: List<MoodEntry>,
    isDark: Boolean,
    cardBg: Color,
    textColor: Color,
    onBack: () -> Unit
) {
    val counts = entries.groupBy { it.moodName }.mapValues { it.value.size }
    val maxCount = counts.values.maxOrNull() ?: 1
    val moodColorMap = (defaultMoods + entries.map {
        CustomMood(it.emoji, it.moodName, it.color)
    }).associate { it.name to Pair(it.emoji, it.color) }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
        TopBar("", isDark, textColor, cardBg, onBack = onBack)
        Text("📊 Mood Chart", fontSize = 26.sp,
            fontWeight = FontWeight.Bold, color = textColor)
        Text("Your mood breakdown", fontSize = 13.sp, color = textColor.copy(0.4f))
        Spacer(Modifier.height(20.dp))

        if (entries.isEmpty()) {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📊", fontSize = 56.sp)
                    Text("No data yet!", fontSize = 18.sp, color = textColor.copy(0.5f))
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(counts.keys.toList()) { moodName ->
                    val count = counts[moodName] ?: 0
                    val info = moodColorMap[moodName]
                    val color = info?.second ?: Purple
                    val emoji = info?.first ?: "😊"
                    val barFraction = count.toFloat() / maxCount

                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(cardBg).padding(14.dp)
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(emoji, fontSize = 22.sp)
                                    Spacer(Modifier.width(8.dp))
                                    Text(moodName, fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium, color = textColor)
                                }
                                Text("$count ${if (count == 1) "time" else "times"}",
                                    fontSize = 13.sp, color = color,
                                    fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.height(8.dp))
                            Box(
                                modifier = Modifier.fillMaxWidth().height(10.dp)
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(color.copy(0.15f))
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxWidth(barFraction)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(5.dp))
                                        .background(color)
                                )
                            }
                        }
                    }
                }
                item { Spacer(Modifier.height(24.dp)) }
            }
        }
    }
}