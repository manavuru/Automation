package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "contacts")
data class Contact(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val phoneNumber: String,
    val status: String = "Active", // "Active", "Inactive"
    val tags: String = "Default"
)

@Entity(tableName = "automation_rules")
data class AutomationRule(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val keyword: String,
    val replyTemplate: String,
    val isAiEnabled: Boolean = false,
    val promptType: String = "Support", // "Support", "Friendly", "Sarcastic", "Formal", "Translator"
    val isActive: Boolean = true
)

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sender: String, // Phone number or "SYSTEM", "YOU", "AI_AUTO"
    val senderName: String,
    val messageText: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isReceived: Boolean = true,
    val sentiment: String = "Neutral", // "Positive", "Neutral", "Negative"
    val translatedText: String? = null,
    val contactPhoneNumber: String = "" // Explicit link to conversation thread partner
)

@Entity(tableName = "campaigns")
data class Campaign(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val messageTemplate: String,
    val scheduledTime: Long,
    val status: String = "Draft", // "Draft", "Scheduled", "Sending", "Completed"
    val sentCount: Int = 0,
    val totalCount: Int = 0
)

@Dao
interface ChatFlowDao {
    // Contacts
    @Query("SELECT * FROM contacts ORDER BY name ASC")
    fun getAllContacts(): Flow<List<Contact>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: Contact)

    @Delete
    suspend fun deleteContact(contact: Contact)

    // Automation Rules
    @Query("SELECT * FROM automation_rules ORDER BY id DESC")
    fun getAllRules(): Flow<List<AutomationRule>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRule(rule: AutomationRule)

    @Delete
    suspend fun deleteRule(rule: AutomationRule)

    // Chat Messages
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<ChatMessage>>

    @Query("SELECT * FROM chat_messages ORDER BY timestamp DESC LIMIT 100")
    fun getRecentMessages(): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage)

    @Query("DELETE FROM chat_messages")
    suspend fun clearAllMessages()

    // Campaigns
    @Query("SELECT * FROM campaigns ORDER BY scheduledTime DESC")
    fun getAllCampaigns(): Flow<List<Campaign>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCampaign(campaign: Campaign)

    @Delete
    suspend fun deleteCampaign(campaign: Campaign)
}
