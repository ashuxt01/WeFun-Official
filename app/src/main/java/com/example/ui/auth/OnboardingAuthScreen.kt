package com.example.ui.auth

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.AlternateEmail
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.LockReset
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.AuthMethod
import com.example.ui.components.GlassCard
import com.example.ui.components.WefunBrandLogo
import com.example.ui.components.WefunGradientButton
import com.example.ui.theme.BorderColor
import com.example.ui.theme.CyanPulse
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.LightLavenderTop
import com.example.ui.theme.LocalWefunColors
import com.example.ui.theme.PinkAccent
import com.example.ui.theme.PremiumGold
import com.example.ui.theme.PrimaryBackground
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.SecondaryBackground
import com.example.ui.theme.SecondaryText

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingAuthScreen(
    isGoogleAuthBusy: Boolean = false,
    onGoogleSignInClick: (() -> Unit)? = null,
    onAuthCompleted: (
        nickname: String,
        username: String,
        phone: String,
        email: String,
        dob: String,
        gender: String,
        country: String,
        language: String,
        photoUri: String?,
    ) -> Unit,
) {
    val wefunColors = LocalWefunColors.current
    var step by remember { mutableIntStateOf(0) } // 0 = Auth, 1 = Profile Setup, 2 = Recovery
    var selectedTab by remember { mutableIntStateOf(0) }

    var phone by remember { mutableStateOf("+91 98765 43210") }
    var otpSent by remember { mutableStateOf(false) }
    var otpCode by remember { mutableStateOf("") }

    var email by remember { mutableStateOf("aria@wefun.live") }
    var password by remember { mutableStateOf("wefun2026") }
    var isRegisterMode by remember { mutableStateOf(false) }

    var nickname by remember { mutableStateOf("Aria Vance") }
    var username by remember { mutableStateOf("ariavance") }
    var dobYear by remember { mutableStateOf("2000") }
    var isAgeConfirmed by remember { mutableStateOf(true) }
    var gender by remember { mutableStateOf("Prefer not to say") }
    var country by remember { mutableStateOf("India") }
    var language by remember { mutableStateOf("English") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var authError by remember { mutableStateOf<String?>(null) }
    var recoverySuccess by remember { mutableStateOf<String?>(null) }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri -> if (uri != null) photoUri = uri }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        LightLavenderTop,
                        PrimaryBackground,
                        SecondaryBackground
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 560.dp)
                .align(Alignment.Center)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Premium Light Official WEFUN Brand Header Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = PrimaryBackground,
                shadowElevation = 6.dp,
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    LightLavenderTop,
                                    PrimaryBackground,
                                    PinkAccent.copy(alpha = 0.08f)
                                )
                            )
                        )
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    WefunBrandLogo(
                        size = 76.dp,
                        cornerRadius = 18.dp,
                        showGoldBorder = true,
                        modifier = Modifier.testTag("onboarding_brand_logo")
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "WEFUN",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = PrimaryText
                        )
                        Text(
                            text = "Connect. Play. Express. Belong.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = PrimaryPurple,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Live Voice Party Rooms, Gifting & Games",
                            style = MaterialTheme.typography.labelSmall,
                            color = SecondaryText
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                shape = RoundedCornerShape(50),
                color = PrimaryPurple.copy(alpha = 0.08f),
                border = BorderStroke(1.dp, PrimaryPurple.copy(alpha = 0.25f))
            ) {
                Text(
                    text = "Google Sign-In & Local Room Persistence Active",
                    style = MaterialTheme.typography.labelSmall,
                    color = PrimaryPurple,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedContent(targetState = step, label = "authStep") { currentStep ->
                when (currentStep) {
                    0 -> {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                TabRow(
                                    selectedTabIndex = selectedTab,
                                    containerColor = Color.Transparent,
                                    contentColor = MaterialTheme.colorScheme.primary
                                ) {
                                    AuthMethod.entries.forEachIndexed { idx, method ->
                                        Tab(
                                            selected = selectedTab == idx,
                                            onClick = {
                                                selectedTab = idx
                                                authError = null
                                            },
                                            text = {
                                                Text(
                                                    text = method.label,
                                                    style = MaterialTheme.typography.labelMedium
                                                )
                                            }
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))

                                when (selectedTab) {
                                    0 -> {
                                        OutlinedTextField(
                                            value = phone,
                                            onValueChange = { phone = it },
                                            label = { Text("Mobile Number") },
                                            leadingIcon = { Icon(Icons.Rounded.PhoneAndroid, null) },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                            singleLine = true,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("auth_phone_input")
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        if (otpSent) {
                                            OutlinedTextField(
                                                value = otpCode,
                                                onValueChange = { otpCode = it.take(6) },
                                                label = { Text("6-Digit OTP (Demo: 123456)") },
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                                singleLine = true,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .testTag("auth_otp_input")
                                            )
                                            Spacer(modifier = Modifier.height(12.dp))
                                            WefunGradientButton(
                                                text = "Verify OTP & Continue",
                                                testTag = "verify_otp_button",
                                                onClick = {
                                                    if (otpCode.length >= 4) {
                                                        authError = null
                                                        step = 1
                                                    } else {
                                                        authError = "Enter at least 4 digits (Demo OTP: 123456)"
                                                    }
                                                }
                                            )
                                        } else {
                                            WefunGradientButton(
                                                text = "Send OTP Code",
                                                testTag = "send_otp_button",
                                                onClick = {
                                                    if (phone.filter { it.isDigit() }.length >= 8) {
                                                        otpSent = true
                                                        otpCode = "123456"
                                                        authError = null
                                                    } else {
                                                        authError = "Enter a valid mobile number"
                                                    }
                                                }
                                            )
                                        }
                                    }

                                    1 -> {
                                        Text(
                                            text = "Sign in with Google",
                                            style = MaterialTheme.typography.titleSmall,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "Authenticate with your Google account via Android Credential Manager to sync your profile and live voice rooms across devices.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = wefunColors.mutedText
                                        )
                                        Spacer(modifier = Modifier.height(14.dp))
                                        WefunGradientButton(
                                            text = if (isGoogleAuthBusy) "Signing in..." else "Sign in with Google",
                                            testTag = "google_signin_button",
                                            enabled = !isGoogleAuthBusy && onGoogleSignInClick != null,
                                            leadingIcon = {
                                                if (isGoogleAuthBusy) {
                                                    CircularProgressIndicator(
                                                        modifier = Modifier.size(18.dp),
                                                        color = Color.White,
                                                        strokeWidth = 2.dp
                                                    )
                                                } else {
                                                    Icon(Icons.Rounded.AccountCircle, null, tint = Color.White)
                                                }
                                            },
                                            onClick = { onGoogleSignInClick?.invoke() }
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        OutlinedButton(
                                            onClick = { step = 1 },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("google_demo_signin_button")
                                        ) {
                                            Text("Continue Offline as Aria Vance")
                                        }
                                    }

                                    2 -> {
                                        OutlinedTextField(
                                            value = email,
                                            onValueChange = { email = it },
                                            label = { Text("Email Address") },
                                            leadingIcon = { Icon(Icons.Rounded.AlternateEmail, null) },
                                            singleLine = true,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        OutlinedTextField(
                                            value = password,
                                            onValueChange = { password = it },
                                            label = { Text("Password") },
                                            visualTransformation = PasswordVisualTransformation(),
                                            singleLine = true,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            TextButton(onClick = { isRegisterMode = !isRegisterMode }) {
                                                Text(if (isRegisterMode) "Already have an account?" else "Create Email Account")
                                            }
                                            TextButton(onClick = { step = 2 }) {
                                                Text("Recover Account")
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        WefunGradientButton(
                                            text = if (isRegisterMode) "Register & Setup Profile" else "Sign In",
                                            testTag = "email_signin_button",
                                            onClick = {
                                                if (email.contains("@") && password.length >= 6) {
                                                    authError = null
                                                    step = 1
                                                } else {
                                                    authError = "Enter a valid email and 6+ char password."
                                                }
                                            }
                                        )
                                    }
                                }

                                if (authError != null) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = authError!!,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }

                    1 -> {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(onClick = { step = 0 }) {
                                        Icon(Icons.Rounded.ArrowBackIosNew, "Back")
                                    }
                                    Column {
                                        Text("Complete Your Identity", style = MaterialTheme.typography.titleLarge)
                                        Text(
                                            "Personalize your WEFUN ID & 18+ safety check",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = wefunColors.mutedText
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(72.dp)
                                            .clip(CircleShape)
                                            .border(2.dp, ElectricViolet, CircleShape)
                                            .clickable {
                                                photoPicker.launch(
                                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                                )
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (photoUri != null) {
                                            AsyncImage(
                                                model = photoUri,
                                                contentDescription = "Selected avatar",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        } else {
                                            Image(
                                                painter = painterResource(id = R.drawable.img_avatar_neon_host_1794578988450),
                                                contentDescription = "Default 3D avatar",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        }
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.BottomEnd)
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(ElectricViolet),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Rounded.CameraAlt, null, tint = Color.White, modifier = Modifier.size(14.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Unique ID: WF-804921", style = MaterialTheme.typography.labelMedium, color = CyanPulse)
                                        Text(
                                            "Tap photo to pick from device gallery (zero-permission Photo Picker)",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = wefunColors.mutedText
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                                OutlinedTextField(
                                    value = nickname,
                                    onValueChange = { nickname = it },
                                    label = { Text("Nickname") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = username,
                                    onValueChange = { username = it.lowercase().filter { c -> c.isLetterOrDigit() || c == '_' } },
                                    label = { Text("Username (@handle)") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    OutlinedTextField(
                                        value = dobYear,
                                        onValueChange = { dobYear = it.filter { c -> c.isDigit() }.take(4) },
                                        label = { Text("Birth Year") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        singleLine = true,
                                        modifier = Modifier.weight(1f)
                                    )
                                    OutlinedTextField(
                                        value = country,
                                        onValueChange = { country = it },
                                        label = { Text("Country") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                Text("Gender (Optional)", style = MaterialTheme.typography.labelMedium)
                                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    listOf("Female", "Male", "Non-binary", "Prefer not to say").forEach { opt ->
                                        FilterChip(
                                            selected = gender == opt,
                                            onClick = { gender = opt },
                                            label = { Text(opt, style = MaterialTheme.typography.bodySmall) }
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Primary Language", style = MaterialTheme.typography.labelMedium)
                                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    listOf("English", "Hindi", "Arabic", "Spanish").forEach { lang ->
                                        FilterChip(
                                            selected = language == lang,
                                            onClick = { language = lang },
                                            label = { Text(lang, style = MaterialTheme.typography.bodySmall) }
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "I confirm I am 18+ years old",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Switch(
                                        checked = isAgeConfirmed,
                                        onCheckedChange = { isAgeConfirmed = it }
                                    )
                                }

                                val birthYearInt = dobYear.toIntOrNull() ?: 2000
                                val ageValid = isAgeConfirmed && birthYearInt in 1920..2008
                                if (!ageValid) {
                                    Text(
                                        "You must be 18+ (birth year 2008 or earlier) to enter live voice rooms.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }

                                Spacer(modifier = Modifier.height(14.dp))
                                WefunGradientButton(
                                    text = "Enter WEFUN Universe",
                                    testTag = "enter_wefun_button",
                                    enabled = ageValid && nickname.isNotBlank(),
                                    onClick = {
                                        onAuthCompleted(
                                            nickname,
                                            username,
                                            phone,
                                            email,
                                            "$dobYear-05-14",
                                            gender,
                                            country,
                                            language,
                                            photoUri?.toString()
                                        )
                                    }
                                )
                            }
                        }
                    }

                    else -> {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(onClick = { step = 0 }) {
                                        Icon(Icons.Rounded.ArrowBackIosNew, "Back")
                                    }
                                    Text("Account Recovery", style = MaterialTheme.typography.titleLarge)
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                OutlinedTextField(
                                    value = email,
                                    onValueChange = { email = it },
                                    label = { Text("Registered Email or Phone") },
                                    leadingIcon = { Icon(Icons.Rounded.LockReset, null) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                WefunGradientButton(
                                    text = "Send Recovery Link",
                                    onClick = {
                                        recoverySuccess = "Recovery verification queued for $email (Demo local mode)."
                                    }
                                )
                                if (recoverySuccess != null) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Rounded.CheckCircle, null, tint = wefunColors.emerald, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(recoverySuccess!!, style = MaterialTheme.typography.bodySmall, color = wefunColors.emerald)
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedButton(
                                    onClick = { step = 0 },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Back to Sign In")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
