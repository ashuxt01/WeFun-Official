package com.example.core.database

import android.content.Context
import android.util.Log
import com.example.R
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import org.json.JSONArray
import org.json.JSONObject

enum class OperationType(val value: String) {
    CREATE("create"),
    UPDATE("update"),
    DELETE("delete"),
    LIST("list"),
    GET("get"),
    WRITE("write"),
}

@IgnoreExtraProperties
data class FirestoreUserProfile(
    val userId: String = "",
    val wefunId: String = "",
    val username: String = "",
    val nickname: String = "",
    val email: String = "",
    val phone: String = "",
    val bio: String = "",
    val country: String = "",
    val language: String = "",
    val avatarUrl: String = "",
    val coins: Long = 0L,
    val diamonds: Long = 0L,
    val vipLevel: Long = 0L,
    val userLevel: Long = 1L,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null,
)

@IgnoreExtraProperties
data class FirestoreVoiceRoom(
    val id: String = "",
    val ownerId: String = "",
    val memberUids: List<String> = emptyList(),
    val title: String = "",
    val description: String = "",
    val category: String = "Friends",
    val hostName: String = "",
    val roomLevel: Long = 1L,
    val maxSeats: Long = 8L,
    val isPartnerSeatEnabled: Boolean = false,
    val backdropTheme: String = "ROYAL_BLUE",
    val announcement: String = "",
    val isPasswordLocked: Boolean = false,
    val isHighQualityMode: Boolean = true,
    val banTextChatting: Boolean = false,
    val banPhotoChatting: Boolean = false,
    val banRedPacket: Boolean = false,
    val onlineCount: Long = 1L,
    val popularity: Long = 0L,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null,
)

/**
 * Manages authenticated Cloud Firestore synchronization for the named WeFun database.
 * Collection paths strictly mirror /firebase-blueprint.json and /firestore.rules.
 */
class FirestoreManager(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth,
) {
    constructor(context: Context) : this(
        db = FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        ),
        auth = Firebase.auth,
    )

    fun handleFirestoreError(
        exception: Throwable,
        operationType: OperationType,
        path: String?,
    ): String {
        val causes = generateSequence(exception) { it.cause }
        val rootError = causes.firstOrNull { it is FirebaseFirestoreException }
            ?: causes.firstOrNull { it !is CancellationException }
            ?: exception

        val currentUser = auth.currentUser
        val providerInfoList = currentUser?.providerData?.map { provider ->
            JSONObject().apply {
                put("providerId", provider.providerId)
                put("email", provider.email)
            }
        } ?: emptyList()

        val authInfoJson = JSONObject().apply {
            put("userId", currentUser?.uid)
            put("email", currentUser?.email)
            put("emailVerified", currentUser?.isEmailVerified)
            put("tenantId", currentUser?.tenantId)
            put("providerInfo", JSONArray(providerInfoList))
        }

        val errorInfoJson = JSONObject().apply {
            put("error", rootError.message ?: rootError.toString())
            put("operationType", operationType.value)
            put("path", path)
            put("authInfo", authInfoJson)
        }

        val jsonString = errorInfoJson.toString()
        Log.e("FirestoreError", "Firestore Error: $jsonString")
        return jsonString
    }

    suspend fun createOrUpdateUserProfile(profile: FirestoreUserProfile): Result<Unit> {
        val uid = auth.currentUser?.uid
            ?: return Result.failure(IllegalStateException("User must be signed in before syncing profile."))
        val targetUid = profile.userId.ifBlank { uid }
        val docRef = db.collection(COLLECTION_USERS).document(targetUid)
        return try {
            val existing = docRef.get().await()
            if (existing.exists()) {
                val updates = buildMap<String, Any> {
                    put("wefunId", profile.wefunId)
                    put("username", profile.username)
                    put("nickname", profile.nickname)
                    if (profile.email.isNotEmpty()) put("email", profile.email)
                    if (profile.phone.isNotEmpty()) put("phone", profile.phone)
                    put("bio", profile.bio)
                    put("country", profile.country)
                    put("language", profile.language)
                    if (profile.avatarUrl.isNotEmpty()) put("avatarUrl", profile.avatarUrl)
                    put("coins", profile.coins.coerceAtLeast(0L))
                    put("diamonds", profile.diamonds.coerceAtLeast(0L))
                    put("vipLevel", profile.vipLevel.coerceIn(0L, 12L))
                    put("userLevel", profile.userLevel.coerceIn(1L, 100L))
                    put("updatedAt", FieldValue.serverTimestamp())
                }
                docRef.update(updates).await()
            } else {
                val payload = buildMap<String, Any> {
                    put("userId", targetUid)
                    put("wefunId", profile.wefunId)
                    put("username", profile.username)
                    put("nickname", profile.nickname)
                    if (profile.email.isNotEmpty()) put("email", profile.email)
                    if (profile.phone.isNotEmpty()) put("phone", profile.phone)
                    put("bio", profile.bio)
                    put("country", profile.country)
                    put("language", profile.language)
                    if (profile.avatarUrl.isNotEmpty()) put("avatarUrl", profile.avatarUrl)
                    put("coins", profile.coins.coerceAtLeast(0L))
                    put("diamonds", profile.diamonds.coerceAtLeast(0L))
                    put("vipLevel", profile.vipLevel.coerceIn(0L, 12L))
                    put("userLevel", profile.userLevel.coerceIn(1L, 100L))
                    put("createdAt", FieldValue.serverTimestamp())
                    put("updatedAt", FieldValue.serverTimestamp())
                }
                docRef.set(payload).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun getUserProfile(userId: String): Result<FirestoreUserProfile?> {
        val docRef = db.collection(COLLECTION_USERS).document(userId)
        return try {
            val snap = docRef.get().await()
            Result.success(if (snap.exists()) snap.toUserProfile() else null)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, docRef.path)
            Result.failure(e)
        }
    }

    fun observeUserProfile(userId: String): Flow<FirestoreUserProfile?> {
        val docRef = db.collection(COLLECTION_USERS).document(userId)
        return docRef.snapshots().map { snapshot ->
            if (snapshot.exists()) snapshot.toUserProfile() else null
        }
    }

    suspend fun createVoiceRoom(room: FirestoreVoiceRoom): Result<String> {
        val uid = auth.currentUser?.uid
            ?: return Result.failure(IllegalStateException("User must be signed in to create a room."))
        val docRef = if (room.id.isNotBlank()) {
            db.collection(COLLECTION_VOICE_ROOMS).document(room.id)
        } else {
            db.collection(COLLECTION_VOICE_ROOMS).document()
        }
        val effectiveOwnerId = room.ownerId.ifBlank { uid }
        val normalizedLevel = room.roomLevel.coerceIn(1L, 10L)
        val normalizedMaxSeats = if (normalizedLevel >= 7L) {
            room.maxSeats.coerceIn(8L, 14L)
        } else {
            8L
        }
        val boundedMembers = (listOf(effectiveOwnerId) + room.memberUids)
            .distinct()
            .take(100)

        val payload = buildMap<String, Any> {
            put("ownerId", effectiveOwnerId)
            put("memberUids", boundedMembers)
            put("title", room.title.take(80).ifBlank { "WeFun Party Room" })
            put("description", room.description.take(300))
            put("category", room.category.take(40).ifBlank { "Friends" })
            put("hostName", room.hostName.take(60))
            put("roomLevel", normalizedLevel)
            put("maxSeats", normalizedMaxSeats)
            put("isPartnerSeatEnabled", room.isPartnerSeatEnabled)
            put("backdropTheme", room.backdropTheme.take(40))
            put("announcement", room.announcement.take(500))
            put("isPasswordLocked", room.isPasswordLocked)
            put("isHighQualityMode", room.isHighQualityMode)
            put("banTextChatting", room.banTextChatting)
            put("banPhotoChatting", room.banPhotoChatting)
            put("banRedPacket", room.banRedPacket)
            put("onlineCount", room.onlineCount.coerceAtLeast(0L))
            put("popularity", room.popularity.coerceAtLeast(0L))
            put("createdAt", FieldValue.serverTimestamp())
            put("updatedAt", FieldValue.serverTimestamp())
        }

        return try {
            docRef.set(payload).await()
            Result.success(docRef.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun getVoiceRoom(roomId: String): Result<FirestoreVoiceRoom?> {
        val docRef = db.collection(COLLECTION_VOICE_ROOMS).document(roomId)
        return try {
            val snap = docRef.get().await()
            Result.success(if (snap.exists()) snap.toVoiceRoom() else null)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, docRef.path)
            Result.failure(e)
        }
    }

    fun observeAccessibleRooms(userId: String): Flow<List<FirestoreVoiceRoom>> {
        return db.collection(COLLECTION_VOICE_ROOMS)
            .whereArrayContains("memberUids", userId)
            .snapshots()
            .map { querySnapshot ->
                querySnapshot.documents.map { it.toVoiceRoom() }
                    .sortedByDescending { it.updatedAt ?: it.createdAt ?: Timestamp(0, 0) }
            }
    }

    fun observeVoiceRoom(roomId: String): Flow<FirestoreVoiceRoom?> {
        val docRef = db.collection(COLLECTION_VOICE_ROOMS).document(roomId)
        return docRef.snapshots().map { snapshot ->
            if (snapshot.exists()) snapshot.toVoiceRoom() else null
        }
    }

    suspend fun updatePartnerSeatConfig(roomId: String, enabled: Boolean): Result<Unit> {
        val docRef = db.collection(COLLECTION_VOICE_ROOMS).document(roomId)
        return try {
            docRef.update(
                mapOf(
                    "isPartnerSeatEnabled" to enabled,
                    "updatedAt" to FieldValue.serverTimestamp(),
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun updateRoomSeatCapacity(
        roomId: String,
        roomLevel: Long,
        maxSeats: Long,
    ): Result<Unit> {
        val docRef = db.collection(COLLECTION_VOICE_ROOMS).document(roomId)
        val clampedLevel = roomLevel.coerceIn(1L, 10L)
        val clampedSeats = if (clampedLevel >= 7L) maxSeats.coerceIn(8L, 14L) else 8L
        return try {
            docRef.update(
                mapOf(
                    "roomLevel" to clampedLevel,
                    "maxSeats" to clampedSeats,
                    "updatedAt" to FieldValue.serverTimestamp(),
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun updateRoomActivity(
        roomId: String,
        onlineCount: Long,
        popularity: Long,
    ): Result<Unit> {
        val docRef = db.collection(COLLECTION_VOICE_ROOMS).document(roomId)
        return try {
            docRef.update(
                mapOf(
                    "onlineCount" to onlineCount.coerceAtLeast(0L),
                    "popularity" to popularity.coerceAtLeast(0L),
                    "updatedAt" to FieldValue.serverTimestamp(),
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun upsertVoiceRoomMerge(room: FirestoreVoiceRoom): Result<Unit> {
        val docRef = db.collection(COLLECTION_VOICE_ROOMS).document(room.id)
        val payload = buildMap<String, Any> {
            put("title", room.title.take(80))
            put("description", room.description.take(300))
            put("category", room.category.take(40))
            put("roomLevel", room.roomLevel.coerceIn(1L, 10L))
            put(
                "maxSeats",
                if (room.roomLevel >= 7L) room.maxSeats.coerceIn(8L, 14L) else 8L
            )
            put("isPartnerSeatEnabled", room.isPartnerSeatEnabled)
            put("backdropTheme", room.backdropTheme.take(40))
            put("announcement", room.announcement.take(500))
            put("isPasswordLocked", room.isPasswordLocked)
            put("isHighQualityMode", room.isHighQualityMode)
            put("banTextChatting", room.banTextChatting)
            put("banPhotoChatting", room.banPhotoChatting)
            put("banRedPacket", room.banRedPacket)
            put("updatedAt", FieldValue.serverTimestamp())
        }
        return try {
            docRef.set(payload, SetOptions.merge()).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun deleteVoiceRoom(roomId: String): Result<Unit> {
        val docRef = db.collection(COLLECTION_VOICE_ROOMS).document(roomId)
        return try {
            docRef.delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            Result.failure(e)
        }
    }

    private fun DocumentSnapshot.toUserProfile(): FirestoreUserProfile {
        val behavior = DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
        return FirestoreUserProfile(
            userId = getString("userId") ?: id,
            wefunId = getString("wefunId") ?: "",
            username = getString("username") ?: "",
            nickname = getString("nickname") ?: "",
            email = getString("email") ?: "",
            phone = getString("phone") ?: "",
            bio = getString("bio") ?: "",
            country = getString("country") ?: "",
            language = getString("language") ?: "",
            avatarUrl = getString("avatarUrl") ?: "",
            coins = getLong("coins") ?: 0L,
            diamonds = getLong("diamonds") ?: 0L,
            vipLevel = getLong("vipLevel") ?: 0L,
            userLevel = getLong("userLevel") ?: 1L,
            createdAt = getTimestamp("createdAt", behavior),
            updatedAt = getTimestamp("updatedAt", behavior),
        )
    }

    private fun DocumentSnapshot.toVoiceRoom(): FirestoreVoiceRoom {
        val behavior = DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
        @Suppress("UNCHECKED_CAST")
        val members = (get("memberUids") as? List<*>)
            ?.mapNotNull { it as? String }
            ?: emptyList()
        return FirestoreVoiceRoom(
            id = id,
            ownerId = getString("ownerId") ?: "",
            memberUids = members,
            title = getString("title") ?: "",
            description = getString("description") ?: "",
            category = getString("category") ?: "Friends",
            hostName = getString("hostName") ?: "",
            roomLevel = getLong("roomLevel") ?: 1L,
            maxSeats = getLong("maxSeats") ?: 8L,
            isPartnerSeatEnabled = getBoolean("isPartnerSeatEnabled") ?: false,
            backdropTheme = getString("backdropTheme") ?: "ROYAL_BLUE",
            announcement = getString("announcement") ?: "",
            isPasswordLocked = getBoolean("isPasswordLocked") ?: false,
            isHighQualityMode = getBoolean("isHighQualityMode") ?: true,
            banTextChatting = getBoolean("banTextChatting") ?: false,
            banPhotoChatting = getBoolean("banPhotoChatting") ?: false,
            banRedPacket = getBoolean("banRedPacket") ?: false,
            onlineCount = getLong("onlineCount") ?: 0L,
            popularity = getLong("popularity") ?: 0L,
            createdAt = getTimestamp("createdAt", behavior),
            updatedAt = getTimestamp("updatedAt", behavior),
        )
    }

    companion object {
        const val COLLECTION_USERS = "users"
        const val COLLECTION_VOICE_ROOMS = "voice_rooms"
    }
}
