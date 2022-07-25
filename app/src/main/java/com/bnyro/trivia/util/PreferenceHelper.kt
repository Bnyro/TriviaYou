package com.bnyro.trivia.util

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.bnyro.trivia.R
import com.bnyro.trivia.obj.Question
import com.bnyro.trivia.obj.Quiz
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper

object PreferenceHelper {
    private lateinit var context: Context
    private lateinit var settings: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private val mapper = ObjectMapper()

    fun setContext(context: Context) {
        this.context = context
        settings = PreferenceManager.getDefaultSharedPreferences(context)
        editor = settings.edit()
    }

    private fun getString(key: String, defaultValue: String): String {
        return settings.getString(key, defaultValue)!!
    }

    fun saveQuiz(name: String, isCreator: Boolean, questions: List<Question>) {
        val quizzes = getQuizzes().toMutableList()
        quizzes += Quiz(name, isCreator, questions)

        val json = mapper.writeValueAsString(quizzes)
        editor.putString(context.getString(R.string.quizzes_key), json).commit()
    }

    fun getQuizzes(): List<Quiz> {
        val json = settings.getString(context.getString(R.string.quizzes_key), "")
        val type = object : TypeReference<List<Quiz>>() {}
        return try {
            mapper.readValue(json, type)
        } catch (e: Exception) {
            e.printStackTrace()
            listOf()
        }
    }

    fun deleteQuiz(index: Int) {
        val quizzes = getQuizzes().toMutableList()
        quizzes.removeAt(index)
        val json = mapper.writeValueAsString(quizzes)
        editor.putString(
            context.getString(R.string.quizzes_key),
            json
        ).commit()
    }

    fun getDifficultyQuery(): String? {
        val difficultyPref = getString(
            context.getString(R.string.difficulty_key),
            context.getString(R.string.difficulty_default)
        )
        return when (difficultyPref) {
            "random" -> null
            else -> difficultyPref
        }
    }

    fun getLimit(): Int {
        return getString(
            context.getString(R.string.limit_key),
            context.getString(R.string.limit_default)
        ).toInt()
    }
}