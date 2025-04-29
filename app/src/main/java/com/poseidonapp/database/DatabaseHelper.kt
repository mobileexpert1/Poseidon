package com.poseidonapp.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.poseidonapp.model.questions.Data
import com.poseidonapp.model.questions.QuestionJson

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    lateinit var context: Context

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_SECTION)
        db.execSQL(CREATE_TABLE_QUESTION)
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS $TABLE_KEY_VALUE (nameid TEXT PRIMARY KEY, namevalue TEXT)"
        )

//        const val CREATE_TABLE_QUESTION = (
//                "CREATE TABLE $TABLE_QUESTION (id INTEGER PRIMARY KEY AUTOINCREMENT, sectionId TEXT, question TEXT, type TEXT, valuesColumn TEXT, answer TEXT)"
//                )

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_QUESTION")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SECTION")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_KEY_VALUE")
        onCreate(db)
    }


    // Insert a key-value pair into the key-value pair table
    fun insertKeyValue(key: String, value: String) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_KEY, key)
        values.put(COLUMN_VALUE, value)
        db.insertWithOnConflict(TABLE_KEY_VALUE, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        db.close()
    }

    // Retrieve all key-value pairs from the key-value pair table into a HashMap
    @SuppressLint("Range")
    fun getAllKeyValuePairs(): HashMap<String, String> {
        val keyValues = HashMap<String, String>()
        val db = this.readableDatabase
        val cursor: Cursor? = db.query(
            TABLE_KEY_VALUE,
            arrayOf(COLUMN_KEY, COLUMN_VALUE),
            null, null, null, null, null
        )
        cursor?.use {
            if (it.moveToFirst()) {
                do {
                    val key = it.getString(it.getColumnIndex("nameid"))
                    val value = it.getString(it.getColumnIndex("namevalue"))
                    keyValues[key] = value
                } while (it.moveToNext())
            }
        }
        cursor?.close()
        db.close()
        return keyValues
    }

    // Delete a key-value pair from the key-value pair table
    fun deleteKeyValue(key: String) {
        val db = this.writableDatabase
        db.delete(TABLE_KEY_VALUE, "$COLUMN_KEY = ?", arrayOf(key))
        db.close()
    }

    fun clearDatabase() {
        val db = this.writableDatabase

        // Delete all rows from the tables
        db.execSQL("DELETE FROM $TABLE_SECTION")
        db.execSQL("DELETE FROM $TABLE_QUESTION")

        // Optionally, you can reset the auto-increment counters
        // db.execSQL("DELETE FROM sqlite_sequence WHERE name='$TABLE_SECTION'")
        // db.execSQL("DELETE FROM sqlite_sequence WHERE name='$TABLE_QUESTION'")

        db.close()
    }

// previous working
    /*


        fun insertSections(sections: List<Data>) {
            val db: SQLiteDatabase = this.writableDatabase
            sections.forEach { section ->

                Log.d("Inserting", "Section: ${section.sectionName}")
                val sectionValues = ContentValues().apply {
    //                put("id", section.id)
    //                put("sectionName", section.sectionName)
    //                put("created_at", section.created_at)
    //                put("isInclude", section.isInclude)
                    put("created_at", section.createdAt)
                    put("id", section.id)
                    put("isInclude", section.isInclude)
                    put("sectionName", section.sectionName)
                    put("systemId", section.systemId)
                    put("systemLocationText", section.systemLocationText)
                }
    //            val sectionId = db.insert(TABLE_SECTION, null, sectionValues)
                db.insert(TABLE_SECTION, null, sectionValues)

                section.questionJson.forEach { question ->
                    val questionValues = ContentValues().apply {
                        put("sectionId", section.id)
                        put("question", question.question)
                        put("type", question.type)
                        put("valuesColumn", question.values)
                        put("answer", question.answer)
                    }
                    db.insert(TABLE_QUESTION, null, questionValues)
                }
            }
            db.close()
        }


        */

    fun insertSections(sections: List<Data>) {
        val db: SQLiteDatabase = this.writableDatabase

        sections.forEachIndexed { index, section ->
            Log.d("Insert Section", "Section ID: ${section.id}, Section Name: ${section.sectionName}")

            // Insert the section into TABLE_SECTION
            val sectionValues = ContentValues().apply {
                put("created_at", section.createdAt)
                put("id", index)
                put("ids",section.id )
                put("isInclude", section.isInclude)
                put("sectionName", section.sectionName)
                put("systemId", section.systemId)
                put("systemLocationText", section.systemLocationText)
            }
            db.insert(TABLE_SECTION, null, sectionValues)
            // Clear existing questions for this section ID
//            db.delete(TABLE_QUESTION, "sectionId = ?", arrayOf(section.id))
            // Insert related questions into TABLE_QUESTION
            section.questionJson.forEach { question ->
                Log.d("Insert Question", "Section ID: ${section.id}, Question: ${question.question}")
                val questionValues = ContentValues().apply {
//                    put("sectionId", section.id)
                    put("sectionId", index)
                    put("question", question.question)
                    put("type", question.type)
                    put("valuesColumn", question.values)
                    put("answer", question.answer)
                }
                db.insert(TABLE_QUESTION, null, questionValues)
            }
        }

        db.close()
    }

    fun updateSectionIsInclude(sectionId: String, newIsIncludeValue: String) {
        val db: SQLiteDatabase = this.writableDatabase

        // Define the values to be updated
        val values = ContentValues().apply {
            put("isInclude", newIsIncludeValue)
        }

        // Define the WHERE clause to identify the specific section by its ID
//        val whereClause = "id = ?"
        val whereClause = "id = ?"
        val whereArgs = arrayOf(sectionId)

        // Update the row in the TABLE_SECTION table
        db.update(TABLE_SECTION, values, whereClause, whereArgs)

        db.close()
    }

    @SuppressLint("Range")
    fun createNewSection(modifiedSectionName: String, sourceSectionId: String) {
        val db = this.writableDatabase
        try {
            // Retrieve the largest integer ID from TABLE_SECTION
            val cursor = db.rawQuery("SELECT MAX(CAST(id AS INTEGER)) FROM $TABLE_SECTION", null)
            var lastId = 0
            if (cursor.moveToFirst()) {
                lastId = cursor.getInt(0)
            }
            cursor.close()

            // Generate a new unique integer ID for the new section
            val newId = lastId + 1

            // Begin a database transaction
            db.beginTransaction()

            try {
                // Insert a new section with the modified section name and the new unique integer ID
                val sectionValues = ContentValues().apply {
//                    put("id", newId.toString()) // Store as text
//                    put("sectionName", modifiedSectionName)
//                    put("created_at", "")
//                    put("isInclude", "")

                    put("created_at", "")
                    put("id", newId.toString())
                    put("ids", newId.toString())
                    put("isInclude", "")
                    put("sectionName", modifiedSectionName)
                    put("systemId", "")
                    put("systemLocationText", "")
                }
                val newSectionId = db.insert(TABLE_SECTION, null, sectionValues)

                // Copy questionJson data from another section to the new section
                val cursorQuestion = db.rawQuery(
                    "SELECT * FROM $TABLE_QUESTION WHERE sectionId=?",
                    arrayOf(sourceSectionId)
                )
                if (cursorQuestion.moveToFirst()) {
                    do {
                        val question =
                            cursorQuestion.getString(cursorQuestion.getColumnIndex("question"))
                        val type = cursorQuestion.getString(cursorQuestion.getColumnIndex("type"))
                        val valuesColumn =
                            cursorQuestion.getString(cursorQuestion.getColumnIndex("valuesColumn"))
                        val answer =
                            cursorQuestion.getString(cursorQuestion.getColumnIndex("answer"))

                        val questionValues = ContentValues().apply {
//                            put("sectionId", newId.toString()) // Store as text
                            put("sectionId", newSectionId) // Store as text
                            put("question", question)
                            put("type", type)
                            put("valuesColumn", valuesColumn)
                            put("answer", answer)
                        }
                        db.insert(TABLE_QUESTION, null, questionValues)
                    } while (cursorQuestion.moveToNext())
                }

                // Commit the transaction if everything is successful
                db.setTransactionSuccessful()
            } catch (e: Exception) {
                // Handle the exception
            } finally {
                // End the transaction and close the database
                db.endTransaction()
            }
        } catch (e: Exception) {
            // Handle the exception
        } finally {
            db.close()
        }
    }


    fun updateQuestionAnswer(sectionId: String, question: String, answer: String) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("answer", answer)
        }
        val whereClause = "sectionId = ? AND question = ?"
        val whereArgs = arrayOf(sectionId, question)
        db.update(TABLE_QUESTION, values, whereClause, whereArgs)
        db.close()
    }

    // Inside your logout function or wherever you handle the logout action
    fun logout() {
        // Clear the database
        context.deleteDatabase(DatabaseHelper.DATABASE_NAME)

        // Perform any other logout-related tasks
    }


    fun getAllSections(): List<Data> {
        var sections = mutableListOf<Data>()
        try {
            val db = this.readableDatabase
            val cursor = db.rawQuery("SELECT * FROM $TABLE_SECTION", null)
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getString(cursor.getColumnIndex("id"))
                    val ids = cursor.getString(cursor.getColumnIndex("ids"))
                    val sectionName = cursor.getString(cursor.getColumnIndex("sectionName"))
                    val created_at = cursor.getString(cursor.getColumnIndex("created_at"))
                    val isInclude = cursor.getString(cursor.getColumnIndex("isInclude"))
                    val systemId = cursor.getString(cursor.getColumnIndex("systemId"))
                    val systemLocationText =
                        cursor.getString(cursor.getColumnIndex("systemLocationText"))
                    val questions = getQuestionsForSection(ids)
//                    val section = DataItem(sectionName, isInclude, questions, created_at, id)
                    val section = Data(
                        createdAt = created_at, id = id, isInclude = isInclude,
                        questionJson = questions, sectionName = sectionName,
                        systemId = systemId, systemLocationText = systemLocationText
                    )
                    sections.add(section)
                } while (cursor.moveToNext())
            } else {
                // If cursor is empty, log a message or handle accordingly
            }
            cursor.close()
            db.close()
        } catch (e: Exception) {
            Log.d("getAllSections", "$e")
            // Handle the exception, e.g., log the error or show a message to the user
        }

        return sections
    }


    /*



      @SuppressLint("Range")
      fun getAllSectionsWithQuestions(): List<Data> {
          val sections = mutableListOf<Data>()
          try {
              val db = this.readableDatabase
              val sectionCursor = db.rawQuery("SELECT * FROM $TABLE_SECTION", null)

              if (sectionCursor.moveToFirst()) {
                  do {
                      val sectionId = sectionCursor.getString(sectionCursor.getColumnIndex("id"))
                      val sectionName = sectionCursor.getString(sectionCursor.getColumnIndex("sectionName"))
                      val created_at = sectionCursor.getString(sectionCursor.getColumnIndex("created_at"))
                      val isInclude = sectionCursor.getString(sectionCursor.getColumnIndex("isInclude"))

                      // Get questions for this section
                      val questions = getQuestionsForSection(sectionId)

  //                    val section = Data(sectionName, isInclude, questions, created_at, sectionId)
                      val section = Data(createdAt = created_at, id = id, isInclude = "", questionJson = "", sectionName = "", systemId = "", systemLocationText = "")
                      sections.add(section)
                  } while (sectionCursor.moveToNext())
              } else {
                  // If the sectionCursor is empty, log a message or handle accordingly
              }

              sectionCursor.close()
              db.close()
          } catch (e: Exception) {
              // Handle the exception, e.g., log the error or show a message to the user
          }

          return sections
      }



      */


    @SuppressLint("Range")
    fun getAllSections1(): List<Data> {
        val sections = mutableListOf<Data>()
        try {
            val db: SQLiteDatabase = this.readableDatabase
            val cursor = db.rawQuery("SELECT * FROM $TABLE_SECTION", null)
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getString(cursor.getColumnIndex("id"))
                    val ids = cursor.getString(cursor.getColumnIndex("ids"))
                    val sectionName = cursor.getString(cursor.getColumnIndex("sectionName"))
                    val created_at = cursor.getString(cursor.getColumnIndex("created_at"))
                    val isInclude = cursor.getString(cursor.getColumnIndex("isInclude"))
                    val systemId = cursor.getString(cursor.getColumnIndex("systemId"))
                    val systemLocationText = cursor.getString(cursor.getColumnIndex("systemLocationText"))
                    val questions = getQuestionsForSections(db, id) // Pass the database instance
//                    val section = Data(sectionName, isInclude, questions, created_at, id)
                    val section = Data(
                        createdAt = created_at,
                        id = ids,
                        isInclude = isInclude,
                        questionJson = questions,
                        sectionName = sectionName,
                        systemId = systemId,
                        systemLocationText = systemLocationText
                    )
                    sections.add(section)
                } while (cursor.moveToNext())
            } else {
                // If cursor is empty, log a message or handle accordingly
            }
            cursor.close()
            db.close()
        } catch (e: Exception) {
            // Handle the exception, e.g., log the error or show a message to the user
        }

        return sections
    }

    @SuppressLint("Range")
    private fun getQuestionsForSections(db: SQLiteDatabase, sectionId: String): List<QuestionJson> {
        val questions = mutableListOf<QuestionJson>()
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_QUESTION WHERE sectionId=? ORDER BY sectionId ASC",
            arrayOf(sectionId)
        )
        if (cursor.moveToFirst()) {
            do {
                val question = cursor.getString(cursor.getColumnIndex("question"))
                val type = cursor.getString(cursor.getColumnIndex("type"))
                val valuesColumn = cursor.getString(cursor.getColumnIndex("valuesColumn"))
                val answer = cursor.getString(cursor.getColumnIndex("answer"))
                val questionObj = QuestionJson(question, answer, valuesColumn, type)
                questions.add(questionObj)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return questions
    }

    @SuppressLint("Range")
    fun getQuestionsForSection(sectionId: String): List<QuestionJson> {
        val questions = mutableListOf<QuestionJson>()
        val db = this.readableDatabase
        val cursor =
            db.rawQuery(
                "SELECT * FROM $TABLE_QUESTION WHERE sectionId=? ORDER BY sectionId ASC",
                arrayOf(sectionId)
            )
        if (cursor.moveToFirst()) {
            do {
                val question = cursor.getString(cursor.getColumnIndex("question"))
                val type = cursor.getString(cursor.getColumnIndex("type"))
                val valuesColumn = cursor.getString(cursor.getColumnIndex("valuesColumn"))
                val answer = cursor.getString(cursor.getColumnIndex("answer"))
                val questionObj = QuestionJson(question, answer, valuesColumn, type)
                questions.add(questionObj)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return questions
    }

    companion object {
        const val DATABASE_NAME = "POSEIDON_DB"
        const val DATABASE_VERSION = 1
        const val TABLE_SECTION = "sections"
        const val TABLE_QUESTION = "questions"
        /*const val CREATE_TABLE_SECTION = (
               "CREATE TABLE $TABLE_SECTION (id TEXT, sectionName TEXT, created_at TEXT, isInclude TEXT)"
               )*/

        const val TABLE_KEY_VALUE = "key_value"
        const val COLUMN_KEY = "nameid"
        const val COLUMN_VALUE = "namevalue"

        const val CREATE_TABLE_SECTION = (
                "CREATE TABLE $TABLE_SECTION (created_at TEXT, id TEXT, ids TEXT, isInclude TEXT, sectionName TEXT, systemId TEXT, systemLocationText TEXT)"
                )


        /*const val CREATE_TABLE_SECTION = (
                "CREATE TABLE $TABLE_SECTION (id TEXT, sectionName TEXT, created_at TEXT, isInclude TEXT)"
                )*/

        /* const val CREATE_TABLE_SECTION = (
                "CREATE TABLE $TABLE_SECTION (id TEXT PRIMARY KEY, sectionName TEXT, created_at TEXT, isInclude TEXT)"
                )*/

        /* const val CREATE_TABLE_SECTION = (
                "CREATE TABLE $TABLE_SECTION (id TEXT PRIMARY KEY, sectionName TEXT, created_at TEXT, isInclude TEXT)"
                )*/

        /* *//* const val CREATE_TABLE_QUESTION = (
                "CREATE TABLE $TABLE_QUESTION (id INTEGER, sectionId TEXT, question TEXT, type TEXT, valuesColumn TEXT, answer TEXT)"
                )*/
        const val CREATE_TABLE_QUESTION = (
                "CREATE TABLE $TABLE_QUESTION (id INTEGER PRIMARY KEY AUTOINCREMENT, sectionId TEXT, question TEXT, type TEXT, valuesColumn TEXT, answer TEXT)"
                )
    }
}
