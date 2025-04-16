package com.example.devcollab.Database.Converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converter {
    @TypeConverter
    fun fromSkillsList(skills: List<String>?): String? {
        return Gson().toJson(skills)
    }

    @TypeConverter
    fun toSkillsList(skillsJson: String?): List<String>? {
        if (skillsJson == null) return null
        val type = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(skillsJson, type)
    }
}