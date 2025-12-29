package com.attri.WordOfDay.util

import android.content.Context
import android.content.Intent
import com.attri.WordOfDay.data.local.entity.WordOfTheDay

fun shareWord(context: Context, word: WordOfTheDay) {
    val synonyms = if (word.synonym.isNotBlank()) "Synonyms: ${word.synonym}" else ""
    val antonyms = if (word.antonym.isNotBlank()) "Antonyms: ${word.antonym}" else ""
    
    val sentences = if (word.sentences.isNotEmpty()) {
        "\nExamples:\n" + word.sentences.joinToString("\n") { "• $it" }
    } else ""

    val shareText = buildString {
        appendLine("📚 *VocabDaily Word of the Day*")
        appendLine()
        appendLine("✨ *${word.word}* (${word.hindiMeaning})")
        appendLine()
        appendLine("📖 *Definition:* ${word.definition}")
        
        if (synonyms.isNotEmpty()) {
            appendLine()
            appendLine("✅ $synonyms")
        }
        
        if (antonyms.isNotEmpty()) {
            appendLine()
            appendLine("❌ $antonyms")
        }
        
        if (sentences.isNotEmpty()) {
            appendLine(sentences)
        }
        
        appendLine()
        appendLine("🚀 _Learn more with VocabDaily!_")
    }

    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, shareText)
        type = "text/plain"
    }
    
    val shareIntent = Intent.createChooser(sendIntent, "Share Word via")
    context.startActivity(shareIntent)
}
