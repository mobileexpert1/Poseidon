package com.poseidonapp.model.inspectionHeadsQuestions

class DataItem(
     val sectionName: String,
     val isInclude: String,
     val questionJson: List<QuestionJsonItem>,
     val created_at: String,
     var id: String
)

