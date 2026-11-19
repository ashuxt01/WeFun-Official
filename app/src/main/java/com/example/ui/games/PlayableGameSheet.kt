package com.example.ui.games

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GameId
import com.example.data.model.PartyGame
import com.example.ui.components.WefunGradientButton
import com.example.ui.theme.CyanPulse
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.EmeraldMint
import com.example.ui.theme.RoseNeon
import com.example.ui.theme.RoyalGold

@Composable
fun PlayableGameDialog(
    game: PartyGame,
    onDismiss: () -> Unit,
    onComplete: (outcome: String, score: Int, coinsWon: Long, xpEarned: Long) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("${game.emoji} ${game.title}") },
        text = {
            when (game.id) {
                GameId.LUDO, GameId.JACKAROO -> LudoExpressMiniGame(game, onComplete)
                GameId.DRAW_AND_GUESS, GameId.PAINT_TO_HIDE -> DrawAndGuessMiniGame(game, onComplete)
                GameId.QUIZ_BATTLE -> QuizBattleMiniGame(game, onComplete)
                GameId.NUMBER_GUESS -> NumberGuessMiniGame(game, onComplete)
                GameId.FAST_TYPING -> FastTypingMiniGame(game, onComplete)
                GameId.PETROL_PUMP_TYCOON -> FuelTycoonMiniGame(game, onComplete)
                else -> PartyRoomTurnMiniGame(game, onComplete)
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Exit Table") }
        }
    )
}

@Composable
private fun LudoExpressMiniGame(
    game: PartyGame,
    onComplete: (String, Int, Long, Long) -> Unit,
) {
    var yourPos by remember { mutableIntStateOf(0) }
    var botPos by remember { mutableIntStateOf(0) }
    var lastDice by remember { mutableIntStateOf(6) }
    var turnNote by remember { mutableStateOf("Tap Roll Dice to race to Tile 20!") }
    val goal = 20

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(turnNote, style = MaterialTheme.typography.bodySmall, color = CyanPulse)
        Text("You: $yourPos / $goal", style = MaterialTheme.typography.labelMedium)
        LinearProgressIndicator(progress = { yourPos / goal.toFloat() }, modifier = Modifier.fillMaxWidth())
        Text("Rival Bot: $botPos / $goal", style = MaterialTheme.typography.labelMedium, color = RoseNeon)
        LinearProgressIndicator(
            progress = { botPos / goal.toFloat() },
            color = RoseNeon,
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = ElectricViolet.copy(alpha = 0.2f),
                border = BorderStroke(1.dp, ElectricViolet)
            ) {
                Text("🎲 $lastDice", fontSize = 22.sp, modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp))
            }
            if (yourPos >= goal || botPos >= goal) {
                val won = yourPos >= goal
                WefunGradientButton(
                    text = if (won) "Claim +${game.rewardCoins} Coins" else "Collect +35 XP",
                    onClick = {
                        onComplete(
                            if (won) "1st Place Victory" else "2nd Place",
                            yourPos * 20,
                            if (won) game.rewardCoins else 25L,
                            if (won) 120L else 35L
                        )
                    },
                    modifier = Modifier.width(180.dp)
                )
            } else {
                WefunGradientButton(
                    text = "Roll Dice",
                    testTag = "ludo_roll_button",
                    onClick = {
                        val d = (1..6).random()
                        val botD = (1..5).random()
                        lastDice = d
                        yourPos = (yourPos + d).coerceAtMost(goal)
                        if (yourPos < goal) {
                            botPos = (botPos + botD).coerceAtMost(goal)
                        }
                        turnNote = when {
                            yourPos >= goal -> "VICTORY! You reached the Home Star!"
                            botPos >= goal -> "Rival finished first! Tap to settle."
                            else -> "You rolled $d (+$d) • Bot rolled $botD"
                        }
                    },
                    modifier = Modifier.width(150.dp)
                )
            }
        }
    }
}

@Composable
private fun DrawAndGuessMiniGame(
    game: PartyGame,
    onComplete: (String, Int, Long, Long) -> Unit,
) {
    val strokes = remember { mutableStateListOf<List<Offset>>() }
    var activeStroke by remember { mutableStateOf<List<Offset>>(emptyList()) }
    var guessText by remember { mutableStateOf("") }
    var solved by remember { mutableStateOf(false) }
    val promptWord = "STAR"

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Prompt: Draw a $promptWord & test live canvas!", style = MaterialTheme.typography.labelLarge, color = CyanPulse)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF0E0A1F))
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset -> activeStroke = listOf(offset) },
                        onDrag = { change, _ ->
                            change.consume()
                            activeStroke = activeStroke + change.position
                        },
                        onDragEnd = {
                            if (activeStroke.isNotEmpty()) strokes.add(activeStroke)
                            activeStroke = emptyList()
                        }
                    )
                }
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                (strokes + listOf(activeStroke)).forEach { pts ->
                    if (pts.size > 1) {
                        val path = Path().apply {
                            moveTo(pts.first().x, pts.first().y)
                            pts.drop(1).forEach { lineTo(it.x, it.y) }
                        }
                        drawPath(path, color = CyanPulse, style = Stroke(width = 7f, cap = StrokeCap.Round))
                    }
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { strokes.clear() }) { Text("Clear") }
            OutlinedTextField(
                value = guessText,
                onValueChange = { guessText = it },
                placeholder = { Text("Guess: $promptWord") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }
        if (solved || strokes.size >= 1) {
            WefunGradientButton(
                text = "Finish Round (+${game.rewardCoins} Coins)",
                onClick = {
                    val bonus = if (guessText.equals(promptWord, ignoreCase = true)) 100 else 50
                    onComplete("Sketch Master", 200 + bonus, game.rewardCoins, 95L)
                }
            )
        }
    }
}

@Composable
private fun QuizBattleMiniGame(
    game: PartyGame,
    onComplete: (String, Int, Long, Long) -> Unit,
) {
    val questions = remember {
        listOf(
            Triple("Which planet is known as the Red Planet?", listOf("Venus", "Mars", "Jupiter"), 1),
            Triple("In music, how many notes are in a standard octave?", listOf("8", "12", "6"), 0),
            Triple("Which protocol powers real-time low-latency voice?", listOf("SMTP", "WebRTC / RTP", "FTP"), 1),
        )
    }
    var qIndex by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var finished by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        if (!finished) {
            val (q, opts, correct) = questions[qIndex]
            Text("Question ${qIndex + 1}/${questions.size} • Score: $score", style = MaterialTheme.typography.labelMedium, color = RoyalGold)
            Text(q, style = MaterialTheme.typography.titleSmall)
            opts.forEachIndexed { idx, ans ->
                OutlinedButton(
                    onClick = {
                        if (idx == correct) score += 100
                        if (qIndex < questions.lastIndex) qIndex++ else finished = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text(ans) }
            }
        } else {
            Text("Quiz Complete! Final Score: $score / 300", style = MaterialTheme.typography.titleMedium, color = EmeraldMint)
            WefunGradientButton(
                text = "Claim Reward (+${if (score >= 200) game.rewardCoins else 40L} Coins)",
                onClick = {
                    val won = score >= 200
                    onComplete(if (won) "Quiz Champion" else "Completed", score, if (won) game.rewardCoins else 40L, 110L)
                }
            )
        }
    }
}

@Composable
private fun NumberGuessMiniGame(
    game: PartyGame,
    onComplete: (String, Int, Long, Long) -> Unit,
) {
    val secret = remember { (1..100).random() }
    var input by remember { mutableStateOf("") }
    var tries by remember { mutableIntStateOf(0) }
    var hint by remember { mutableStateOf("Crack the vault code (1 - 100)!") }
    var cracked by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(hint, style = MaterialTheme.typography.titleSmall, color = if (cracked) EmeraldMint else CyanPulse)
        if (!cracked) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it.filter { c -> c.isDigit() }.take(3) },
                label = { Text("Enter 1-100") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            WefunGradientButton(
                text = "Check Code (Attempt #${tries + 1})",
                onClick = {
                    val guess = input.toIntOrNull() ?: return@WefunGradientButton
                    tries++
                    hint = when {
                        guess == secret -> {
                            cracked = true
                            "VAULT CRACKED in $tries tries! Code was $secret!"
                        }
                        guess < secret -> "Higher than $guess!"
                        else -> "Lower than $guess!"
                    }
                    input = ""
                }
            )
        } else {
            WefunGradientButton(
                text = "Claim Vault (+${game.rewardCoins} Coins)",
                onClick = { onComplete("Codebreaker ($tries tries)", (1000 - tries * 80).coerceAtLeast(100), game.rewardCoins, 90L) }
            )
        }
    }
}

@Composable
private fun FastTypingMiniGame(
    game: PartyGame,
    onComplete: (String, Int, Long, Long) -> Unit,
) {
    val target = "connect play express belong"
    var typed by remember { mutableStateOf("") }
    val startMs = remember { System.currentTimeMillis() }
    val matched = typed.trim().equals(target, ignoreCase = true)

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Type this phrase fast:", style = MaterialTheme.typography.labelMedium, color = CyanPulse)
        Surface(shape = RoundedCornerShape(10.dp), color = ElectricViolet.copy(alpha = 0.16f)) {
            Text(target, style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(10.dp))
        }
        OutlinedTextField(
            value = typed,
            onValueChange = { typed = it },
            label = { Text("Your typing") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        if (matched) {
            val elapsedSec = ((System.currentTimeMillis() - startMs) / 1000.0).coerceAtLeast(1.5)
            val wpm = ((4.0 / elapsedSec) * 60.0).toInt()
            Text("Matched! Speed: $wpm WPM", color = EmeraldMint, style = MaterialTheme.typography.titleSmall)
            WefunGradientButton(
                text = "Claim Speed Win (+${game.rewardCoins} Coins)",
                onClick = { onComplete("$wpm WPM Finish", wpm * 5, game.rewardCoins, 100L) }
            )
        }
    }
}

@Composable
private fun FuelTycoonMiniGame(
    game: PartyGame,
    onComplete: (String, Int, Long, Long) -> Unit,
) {
    var stationCash by remember { mutableIntStateOf(200) }
    var carsServed by remember { mutableIntStateOf(0) }
    var pumpsLevel by remember { mutableIntStateOf(1) }
    val targetCash = 600

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Petrol Pump Simulator • Room Economy", style = MaterialTheme.typography.labelMedium, color = RoyalGold)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Station Cash: $$stationCash / $$targetCash", fontWeight = FontWeight.Bold, color = EmeraldMint)
            Text("Pumps Lv.$pumpsLevel • $carsServed cars", style = MaterialTheme.typography.bodySmall)
        }
        LinearProgressIndicator(
            progress = { (stationCash / targetCash.toFloat()).coerceIn(0f, 1f) },
            color = RoyalGold,
            modifier = Modifier.fillMaxWidth()
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    carsServed += 1
                    stationCash += 75 * pumpsLevel
                },
                modifier = Modifier.weight(1f)
            ) { Text("⛽ Fuel Car (+$${75 * pumpsLevel})") }
            OutlinedButton(
                onClick = {
                    if (stationCash >= 150) {
                        stationCash -= 150
                        pumpsLevel++
                    }
                },
                enabled = stationCash >= 150
            ) { Text("Upgrade ($150)") }
        }
        if (stationCash >= targetCash) {
            WefunGradientButton(
                text = "Cash Out +${game.rewardCoins} Coins",
                onClick = { onComplete("Tycoon Goal Hit", stationCash, game.rewardCoins, 130L) }
            )
        }
    }
}

@Composable
private fun PartyRoomTurnMiniGame(
    game: PartyGame,
    onComplete: (String, Int, Long, Long) -> Unit,
) {
    val prompt = remember(game.id) {
        when (game.id) {
            GameId.WHOS_THE_SPY -> "Secret Word: 'ECLIPSE'. Describe it in one clue without saying Sun or Moon!"
            GameId.SPACE_WEREWOLF -> "Task Phase: Calibrate Reactor #3 & watch who vents near Cargo Bay!"
            GameId.TRUTH_OR_DARE -> "Truth: What's the best song you've heard in a WEFUN room this week?"
            GameId.WORD_CHALLENGE -> "Unscramble: 'A T S R L I H G T' → STARLIGHT"
            GameId.MEMORY_CHALLENGE -> "Memorize Pattern: Violet → Cyan → Gold → Violet!"
            GameId.SINGING_COMP, GameId.MIC_GRAB_KARAOKE -> "Beat Drops! Hold the mic and deliver your 15-second chorus!"
            GameId.TEAM_STRATEGY -> "Squad Command: Capture North Crystal Node before Alpha Squad!"
            GameId.HIDE_AND_SEEK -> "Pick Pod #3! Seeker scanner sweeping in 5... 4... 3..."
            GameId.TRICKSTERS_CAFE -> "Night Shift: Serve Espresso Royale to VIP #2 while spotting who swapped the Ace card!"
            GameId.OH_MY_CARD -> "Your Hand: Blue 7 • Wild +4 • Reverse! Play a matching card to drop to 1 card left!"
            else -> game.rules
        }
    }
    var roundScore by remember { mutableIntStateOf(120) }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(game.accentHex).copy(alpha = 0.16f),
            border = BorderStroke(1.dp, Color(game.accentHex))
        ) {
            Text(prompt, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(12.dp))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Round Score: $roundScore", style = MaterialTheme.typography.titleSmall, color = RoyalGold)
            OutlinedButton(onClick = { roundScore += 80 }) { Text("Perform Action (+80)") }
        }
        WefunGradientButton(
            text = "Complete Round (+${game.rewardCoins} Coins)",
            onClick = { onComplete("Room MVP", roundScore, game.rewardCoins, 100L) }
        )
    }
}
