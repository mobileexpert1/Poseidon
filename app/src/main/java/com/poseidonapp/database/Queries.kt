package com.poseidonapp.database

class Queries {


    val CUSTOMER = "create table tbCustomer(id INTEGER PRIMARY KEY AUTOINCREMENT, Key TEXT, " +
            "Number TEXT, Name TEXT, Responsibility_Center TEXT, Location_Code TEXT, Post_Code TEXT," +
            " Country_Region_Code TEXT, Contact TEXT, Salesperson_Code TEXT, Customer_Posting_Group TEXT," +
            " Gen_Bus_Posting_Group TEXT, Customer_Disc_Group TEXT, Payment_Terms_Code TEXT, Language_Code, " +
            "Search_Name TEXT, Credit_Limit_LCY TEXT, Blocked TEXT, Balance_LCY TEXT, Balance_Due_LCY TEXT," +
            " Sales_LCY TEXT, Address TEXT, City TEXT, Allow_Line_Disc TEXT, Last_Date_Modified TEXT," +
            " B2B TEXT, Phone_No TEXT, E_Mail TEXT, Signature_File TEXT)"

    val questionsList= "create table tbquestionsList(id INTEGER PRIMARY KEY AUTOINCREMENT, sectionName TEXT, isInclude TEXT, questionJson TEXT, createdAt TEXT)"
    val questionsListJson= "create table tbquestionsListJson(id INTEGER PRIMARY KEY AUTOINCREMENT, question TEXT, answer TEXT, values TEXT, type TEXT)"

    


}