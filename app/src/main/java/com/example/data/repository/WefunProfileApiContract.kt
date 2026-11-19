package com.example.data.repository

import com.example.data.model.AcceptedRelationship
import com.example.data.model.AdvancedRoomPreview
import com.example.data.model.GiftWallSummary
import com.example.data.model.GuardRelationship
import com.example.data.model.ProfileGiftSendResult
import com.example.data.model.ProfileStatistics
import com.example.data.model.SpecialEffect
import com.example.data.model.UserProfile
import com.example.data.model.VirtualGift
import kotlinx.coroutines.flow.Flow

/**
 * Explicit backend contract for the WeFun Profile, Gift Wall, BFF Relationship,
 * Guard, Advanced Room, Statistics, and 21-Level Charm & Effects ecosystem.
 *
 * Local vs Remote Sync Capability Status:
 * - Current Runtime Backing: Local Room SQLite (`WefunDatabase` v5) + Reactive Kotlin Flows
 *   (plus optional `FirestoreManager` profile mirror when signed in).
 * - Remote Cloud Endpoints Required For Full Production Deployment:
 *   1. `PUT /v1/users/{uid}/profile` & `PATCH /v1/users/{uid}/signature` (requires server actor == owner JWT check)
 *   2. `POST /v1/gifts/send` (server atomic wallet debit + recipient Gift Wall & Charm point credit)
 *   3. `GET /v1/users/{uid}/gift-wall` (paginated & sorted recipient gift ledger)
 *   4. `POST /v1/relationships/{id}/cancel` (participant authorization check)
 *   5. `GET /v1/users/{uid}/guards` & `POST /v1/effects/{id}/toggle` (authorized Guard / Store entitlement verification)
 */
interface WefunProfileApiContract {
    fun observeGiftWall(ownerUid: String): Flow<GiftWallSummary>
    fun observeAuthorizedGuards(ownerUid: String): Flow<List<GuardRelationship>>
    fun observeSpecialEffects(ownerUid: String, userCharmLevel: Int, userActiveLevel: Int): Flow<List<SpecialEffect>>
    fun observeAdvancedRooms(ownerUid: String): Flow<List<AdvancedRoomPreview>>
    fun observeProfileStatistics(profile: UserProfile): Flow<ProfileStatistics>

    suspend fun updateProfileWithOwnershipCheck(
        actorUid: String,
        targetUid: String,
        nickname: String,
        bio: String,
        country: String,
        language: String,
        showGender: Boolean,
        showAge: Boolean,
        photoUri: String?,
        coverPhotoUri: String?,
    ): Result<UserProfile>

    suspend fun updateUserSignature(
        actorUid: String,
        targetUid: String,
        rawSignature: String,
    ): Result<String>

    suspend fun sendProfileGift(
        targetUser: UserProfile,
        gift: VirtualGift,
        quantity: Int,
        fromPackage: Boolean,
        referenceId: String,
    ): Result<ProfileGiftSendResult>

    fun cancelAcceptedRelationship(
        actorUid: String,
        relationshipId: String,
    ): Result<AcceptedRelationship>

    suspend fun toggleSpecialEffectActivation(
        actorUid: String,
        effectId: String,
    ): Result<SpecialEffect>
}
