package com.example.ui.rooms

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.HelpOutline
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AuctionSessionState
import com.example.data.model.BiddingMode
import com.example.data.model.RelationshipTagOption
import com.example.data.model.RoomSeat
import com.example.data.model.StartingToken
import com.example.data.repository.WefunSeedData
import com.example.ui.components.RelationshipTag3DIcon
import com.example.ui.components.StartingToken3DArt
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.PremiumGold
import com.example.ui.theme.TurquoiseActive

private val SheetCharcoalBg = Color(0xFF19191E)
private val SheetTrackBg = Color(0xFF25252C)
private val SheetSelectedToggleBg = Color(0xFF53535C)
private val SheetChipBg = Color(0xFF2B2B33)
private val SheetMutedText = Color(0xFFA2A4B0)

/**
 * Dark charcoal Relationship & Bidding Settings modal sheet strictly scoped to Auction Rooms.
 * Matches the dark Settings modal reference: Ordinary bidding / BFF segmented control,
 * Custom | edit + relationship tag chips, Starting Price ? + reward tokens, and Turquoise Confirm pill.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RelationshipSettingsSheet(
    auctionState: AuctionSessionState,
    customTags: List<RelationshipTagOption>,
    occupiedSeats: List<RoomSeat>,
    onCreateCustomTag: (label: String, mode: BiddingMode) -> Result<RelationshipTagOption>,
    onConfirm: (
        mode: BiddingMode,
        tag: RelationshipTagOption?,
        customLabel: String,
        token: StartingToken?,
        targetUserId: String?,
        targetUserName: String?,
        startingPrice: Long,
    ) -> Unit,
    onDismiss: () -> Unit,
) {
    var mode by remember { mutableStateOf(auctionState.biddingMode) }
    var selectedTag by remember { mutableStateOf(auctionState.selectedTag) }
    var customLabel by remember { mutableStateOf(auctionState.customTagLabel) }
    var selectedToken by remember { mutableStateOf(auctionState.selectedToken) }
    var startingPrice by remember { mutableLongStateOf(auctionState.startingPricePoints.coerceAtLeast(100L)) }
    var showMoreLabels by remember { mutableStateOf(false) }
    var showCustomTagDialog by remember { mutableStateOf(false) }
    var showTokenPickerDialog by remember { mutableStateOf(false) }
    var showStartingPriceHelp by remember { mutableStateOf(false) }
    var validationError by remember { mutableStateOf<String?>(null) }

    val availableTargets = remember(occupiedSeats) {
        occupiedSeats.filter { it.occupantId != null }
            .ifEmpty {
                listOf(
                    RoomSeat(0, occupantId = "host_rhea", occupantName = "DJ Rhea", occupantInitials = "DR", vipLevel = 5),
                    RoomSeat(1, occupantId = "host_kai", occupantName = "Kai Storm", occupantInitials = "KS", vipLevel = 6),
                )
            }
    }
    var selectedTarget by remember(availableTargets) {
        mutableStateOf(
            availableTargets.firstOrNull { it.occupantId == auctionState.targetUserId }
                ?: availableTargets.firstOrNull()
        )
    }

    val ordinaryPrimary = remember { WefunSeedData.ordinaryRelationshipTags.take(6) }
    val ordinaryMore = remember { WefunSeedData.ordinaryRelationshipTags.drop(6) }
    val bffTags = remember { WefunSeedData.bffRelationshipTags }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SheetCharcoalBg,
        contentColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 26.dp)
                .testTag("relationship_settings_sheet"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            // Segmented Toggle: Ordinary bidding vs BFF
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(50))
                    .background(SheetTrackBg)
                    .padding(3.dp)
            ) {
                listOf(
                    BiddingMode.ORDINARY_BIDDING to "Ordinary bidding",
                    BiddingMode.BFF to "BFF",
                ).forEach { (itemMode, label) ->
                    val isSelected = mode == itemMode
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .clip(RoundedCornerShape(50))
                            .background(if (isSelected) SheetSelectedToggleBg else Color.Transparent)
                            .clickable {
                                mode = itemMode
                                validationError = null
                                if (selectedTag?.mode != itemMode) {
                                    selectedTag = if (itemMode == BiddingMode.BFF) bffTags.firstOrNull() else ordinaryPrimary.firstOrNull()
                                }
                            }
                            .testTag(if (itemMode == BiddingMode.BFF) "bidding_mode_bff" else "bidding_mode_ordinary"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) Color.White else SheetMutedText,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Relationship Tag Grid
            if (mode == BiddingMode.ORDINARY_BIDDING) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Custom | [edit] chip
                    val isCustomSelected = customLabel.isNotBlank() && selectedTag?.isCustom == true
                    Surface(
                        onClick = { showCustomTagDialog = true },
                        shape = RoundedCornerShape(50),
                        color = if (isCustomSelected) TurquoiseActive.copy(alpha = 0.24f) else SheetChipBg,
                        border = BorderStroke(1.dp, if (isCustomSelected) TurquoiseActive else Color.Transparent),
                        modifier = Modifier.testTag("custom_tag_chip")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = if (customLabel.isNotBlank()) customLabel else "Custom",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(14.dp)
                                    .background(Color.White.copy(alpha = 0.25f))
                            )
                            Icon(
                                imageVector = Icons.Rounded.Edit,
                                contentDescription = "Create custom relationship tag",
                                tint = SheetMutedText,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }

                    ordinaryPrimary.forEach { tag ->
                        DarkRelationshipChip(
                            tag = tag,
                            isSelected = selectedTag?.id == tag.id,
                            showIcon = false,
                            onClick = {
                                selectedTag = tag
                                customLabel = ""
                                validationError = null
                            }
                        )
                    }

                    customTags.filter { it.mode == BiddingMode.ORDINARY_BIDDING }.forEach { tag ->
                        DarkRelationshipChip(
                            tag = tag,
                            isSelected = selectedTag?.id == tag.id,
                            showIcon = true,
                            onClick = {
                                selectedTag = tag
                                customLabel = tag.label
                                validationError = null
                            }
                        )
                    }
                }

                // Expandable "More Labels"
                TextButton(
                    onClick = { showMoreLabels = !showMoreLabels },
                    modifier = Modifier.testTag("toggle_more_labels_button")
                ) {
                    Text(
                        text = if (showMoreLabels) "Hide More Labels" else "More Labels",
                        color = TurquoiseActive,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                if (showMoreLabels) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ordinaryMore.forEach { tag ->
                            DarkRelationshipChip(
                                tag = tag,
                                isSelected = selectedTag?.id == tag.id,
                                showIcon = false,
                                onClick = {
                                    selectedTag = tag
                                    customLabel = ""
                                    validationError = null
                                }
                            )
                        }
                    }
                }
            } else {
                // BFF mode chips with mini 3D vector badges
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    bffTags.forEach { tag ->
                        DarkRelationshipChip(
                            tag = tag,
                            isSelected = selectedTag?.id == tag.id,
                            showIcon = true,
                            onClick = {
                                selectedTag = tag
                                customLabel = ""
                                validationError = null
                            }
                        )
                    }
                    customTags.filter { it.mode == BiddingMode.BFF }.forEach { tag ->
                        DarkRelationshipChip(
                            tag = tag,
                            isSelected = selectedTag?.id == tag.id,
                            showIcon = true,
                            onClick = {
                                selectedTag = tag
                                customLabel = tag.label
                                validationError = null
                            }
                        )
                    }
                }
            }

            // Target Participant Selector
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Relationship Partner",
                    style = MaterialTheme.typography.labelMedium,
                    color = SheetMutedText
                )
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    availableTargets.forEach { seat ->
                        val picked = selectedTarget?.occupantId == seat.occupantId
                        FilterChip(
                            selected = picked,
                            onClick = { selectedTarget = seat },
                            label = { Text(seat.occupantName ?: "Member", fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = SheetChipBg,
                                labelColor = Color.White,
                                selectedContainerColor = TurquoiseActive.copy(alpha = 0.25f),
                                selectedLabelColor = TurquoiseActive
                            )
                        )
                    }
                }
            }

            // Starting Price (?) + Token Picker + 3 Reward Icons
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Starting Price",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    IconButton(
                        onClick = { showStartingPriceHelp = true },
                        modifier = Modifier.size(24.dp).testTag("starting_price_help_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.HelpOutline,
                            contentDescription = "Starting price explanation",
                            tint = SheetMutedText,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // + Rounded Square Slot (Selected token or '+')
                    Box(
                        modifier = Modifier
                            .size(84.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(SheetTrackBg)
                            .border(
                                width = 1.2.dp,
                                color = if (selectedToken != null) TurquoiseActive else Color.White.copy(alpha = 0.16f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { showTokenPickerDialog = true }
                            .testTag("starting_token_picker"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedToken == null) {
                            Icon(Icons.Rounded.Add, "Select starting token", tint = SheetMutedText, modifier = Modifier.size(32.dp))
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                StartingToken3DArt(token = selectedToken!!, size = 44.dp)
                                Text(
                                    text = "${selectedToken!!.unlockPoints} pts",
                                    fontSize = 10.sp,
                                    color = PremiumGold,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    // Token showcase & unlock notice (in BFF or anytime when configured)
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StartingToken.entries.forEach { tok ->
                                val isActive = selectedToken == tok
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isActive) TurquoiseActive.copy(alpha = 0.18f) else Color.Transparent)
                                        .clickable {
                                            selectedToken = tok
                                            startingPrice = tok.unlockPoints
                                            validationError = null
                                        }
                                        .padding(4.dp)
                                        .testTag("token_option_${tok.id}")
                                ) {
                                    StartingToken3DArt(token = tok, size = 38.dp)
                                    Text(
                                        text = tok.title.removeSuffix(" Token"),
                                        fontSize = 9.sp,
                                        color = if (isActive) TurquoiseActive else SheetMutedText
                                    )
                                }
                            }
                        }

                        if (mode == BiddingMode.BFF || selectedToken != null) {
                            Text(
                                text = "You can unlock eligible rewards when the bidding process is successfully completed.",
                                fontSize = 11.sp,
                                color = SheetMutedText,
                                textAlign = TextAlign.Center,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }

            if (validationError != null) {
                Text(
                    text = validationError!!,
                    color = ErrorRed,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Full-width Turquoise Confirm CTA
            Button(
                onClick = {
                    if (selectedTag == null && customLabel.isBlank()) {
                        validationError = "Please select a relationship tag or create a custom label."
                        return@Button
                    }
                    onConfirm(
                        mode,
                        selectedTag,
                        customLabel,
                        selectedToken,
                        selectedTarget?.occupantId,
                        selectedTarget?.occupantName,
                        selectedToken?.unlockPoints ?: startingPrice
                    )
                    onDismiss()
                },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TurquoiseActive,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("confirm_relationship_settings_button")
            ) {
                Text(
                    text = "Confirm",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    if (showCustomTagDialog) {
        CustomTagCreationDialog(
            mode = mode,
            initialText = customLabel,
            onValidateAndSave = onCreateCustomTag,
            onSuccess = { created ->
                selectedTag = created
                customLabel = created.label
                showCustomTagDialog = false
                validationError = null
            },
            onDismiss = { showCustomTagDialog = false }
        )
    }

    if (showTokenPickerDialog) {
        AlertDialog(
            onDismissRequest = { showTokenPickerDialog = false },
            containerColor = SheetCharcoalBg,
            titleContentColor = Color.White,
            textContentColor = Color.White,
            title = { Text("Select Starting Reward Token", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    StartingToken.entries.forEach { tok ->
                        val picked = selectedToken == tok
                        Surface(
                            onClick = {
                                selectedToken = tok
                                startingPrice = tok.unlockPoints
                                showTokenPickerDialog = false
                            },
                            shape = RoundedCornerShape(14.dp),
                            color = if (picked) TurquoiseActive.copy(alpha = 0.22f) else SheetChipBg,
                            border = BorderStroke(1.dp, if (picked) TurquoiseActive else Color.Transparent)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    StartingToken3DArt(token = tok, size = 36.dp)
                                    Column {
                                        Text(tok.title, fontWeight = FontWeight.Bold, color = Color.White)
                                        Text("Starts at ${tok.unlockPoints} virtual pts", fontSize = 11.sp, color = PremiumGold)
                                    }
                                }
                                if (picked) {
                                    Icon(Icons.Rounded.Check, null, tint = TurquoiseActive)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showTokenPickerDialog = false }) { Text("Done", color = TurquoiseActive) }
            }
        )
    }

    if (showStartingPriceHelp) {
        AlertDialog(
            onDismissRequest = { showStartingPriceHelp = false },
            containerColor = SheetCharcoalBg,
            titleContentColor = Color.White,
            textContentColor = Color.White,
            title = { Text("Starting Price & Tokens") },
            text = {
                Text(
                    "Choose a starting reward token using virtual points. Once bidding succeeds and the target member accepts your invitation, the relationship badge activates for both profiles."
                )
            },
            confirmButton = {
                TextButton(onClick = { showStartingPriceHelp = false }) { Text("Got it", color = TurquoiseActive) }
            }
        )
    }
}

@Composable
private fun DarkRelationshipChip(
    tag: RelationshipTagOption,
    isSelected: Boolean,
    showIcon: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        color = if (isSelected) TurquoiseActive.copy(alpha = 0.24f) else SheetChipBg,
        border = BorderStroke(1.dp, if (isSelected) TurquoiseActive else Color.Transparent),
        modifier = Modifier.testTag("rel_tag_${tag.label.lowercase().replace(" ", "_")}")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (showIcon) {
                RelationshipTag3DIcon(iconKey = tag.iconKey, size = 16.dp)
            }
            Text(
                text = tag.label,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

@Composable
private fun CustomTagCreationDialog(
    mode: BiddingMode,
    initialText: String,
    onValidateAndSave: (label: String, mode: BiddingMode) -> Result<RelationshipTagOption>,
    onSuccess: (RelationshipTagOption) -> Unit,
    onDismiss: () -> Unit,
) {
    var tagInput by remember { mutableStateOf(initialText) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SheetCharcoalBg,
        titleContentColor = Color.White,
        textContentColor = Color.White,
        title = { Text("Create Custom Relationship Tag", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Max 20 characters. Inappropriate or duplicate labels are blocked.",
                    fontSize = 12.sp,
                    color = SheetMutedText
                )
                OutlinedTextField(
                    value = tagInput,
                    onValueChange = {
                        tagInput = it.take(24)
                        errorMsg = null
                    },
                    label = { Text("Custom Tag Name") },
                    placeholder = { Text("e.g., Soul Twin") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("custom_tag_input")
                )

                // Live preview chip
                Surface(
                    shape = RoundedCornerShape(50),
                    color = TurquoiseActive.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, TurquoiseActive)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        RelationshipTag3DIcon(iconKey = "clover", size = 16.dp)
                        Text(
                            text = tagInput.trim().ifBlank { "Preview Tag" },
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (errorMsg != null) {
                    Text(errorMsg!!, color = ErrorRed, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onValidateAndSave(tagInput, mode)
                        .onSuccess { onSuccess(it) }
                        .onFailure { errorMsg = it.message ?: "Invalid tag name." }
                },
                colors = ButtonDefaults.buttonColors(containerColor = TurquoiseActive),
                modifier = Modifier.testTag("save_custom_tag_button")
            ) {
                Text("Save Tag", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Icon(Icons.Rounded.Close, null, tint = SheetMutedText, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Cancel", color = SheetMutedText)
            }
        }
    )
}
