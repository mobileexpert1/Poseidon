package com.poseidonapp.retrofit


import com.poseidonapp.model.addAddress.AddAddressResponse
import com.poseidonapp.model.addOrganization.AddOrganizationResponse
import com.poseidonapp.model.addRequest.AddRequestResponse
import com.poseidonapp.model.addSystemreport.AddSystemFromReportResponse
import com.poseidonapp.model.addbuilding.AddBuildingResponse
import com.poseidonapp.model.addchat.AddChatResponse
import com.poseidonapp.model.addexpense.AddExpenseResponse
import com.poseidonapp.model.address.AddressResponse
import com.poseidonapp.model.addsystemManagement.AddSystemResponse
import com.poseidonapp.model.applyleave.ApplyLeavesResponse
import com.poseidonapp.model.building.BuildingResponse
import com.poseidonapp.model.chatNotes.ChatNotesResponse
import com.poseidonapp.model.chatmessage.ChatMessageResponse
import com.poseidonapp.model.checkroute.CheckRouteResponse
import com.poseidonapp.model.clockInlistscreen.ClockInListScreenResponse
import com.poseidonapp.model.clockinlist.ClockInListResponse
import com.poseidonapp.model.clockinswitch.SwitchClockInResponse
import com.poseidonapp.model.completedrequest.CompletedRequestsResponse
import com.poseidonapp.model.createproject.CreateProjectResponse
import com.poseidonapp.model.dashboard.DashboardResponse
import com.poseidonapp.model.dayclockin.DayClockInResponse
import com.poseidonapp.model.deleteemergencyrequest.DeletedEmergencyRequestResponse
import com.poseidonapp.model.emergencyrequests.EmergencyRequestResponse
import com.poseidonapp.model.feedback.FeedBackResponse
import com.poseidonapp.model.getleaves.GetLeavesResponse
import com.poseidonapp.model.homepage.HomeResponse
import com.poseidonapp.model.inspection.InspectionResponseData
import com.poseidonapp.model.inspectionDetail.IspectionDetailListResponse
import com.poseidonapp.model.inspectionHeadsQuestions.QuestionsListResponse
import com.poseidonapp.model.inspectionnotification.InspectionNotificationResponse
import com.poseidonapp.model.inspectoracceptreject.AcceptRejectResponse
import com.poseidonapp.model.inspectorresportclone.InpectorReportClonResponse
import com.poseidonapp.model.installationAddAssigned.InstallationAddAssignedResponse
import com.poseidonapp.model.installationAssigned.InstallationAssignedResponse
import com.poseidonapp.model.installationPDF.InstallationPDFResponse
import com.poseidonapp.model.login.LoginResponse
import com.poseidonapp.model.logout.LogoutResponse
import com.poseidonapp.model.meetingupdate.MeetingUpdateResponse
import com.poseidonapp.model.onRoute.OnRouteResponse
import com.poseidonapp.model.organization.OrganizationResponse
import com.poseidonapp.model.pendingrequest.PendingRequestResponse
import com.poseidonapp.model.questions.QuestionsResponse
import com.poseidonapp.model.resetPassword.ResetPasswordResponse
import com.poseidonapp.model.respondmeetingrequest.RespondMeetingRequestResponse
import com.poseidonapp.model.safetymeetings.SafetyMeetingsResponse
import com.poseidonapp.model.schedules.ScheduleResponse
import com.poseidonapp.model.systemManagement.SystemManagementResponse
import com.poseidonapp.model.uploadimage.UploadResponse
import com.poseidonapp.model.userProfile.ProfileResponse
import com.poseidonapp.model.weektimesheet.WeekTimeSheetResponse
import retrofit2.Response
import retrofit2.http.*
import java.time.LocalDate


interface ApiInterface {

    @FormUrlEncoded
    @POST("login")
    suspend fun loginRequest(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("deviceToken") device_token: String,
    ): Response<LoginResponse>

    @FormUrlEncoded
    @POST("logout")
    suspend fun logOutRequest(
        @Field("sessionToken") sessionToken: String
    ): Response<LogoutResponse>

    /* @FormUrlEncoded
       @POST("inspectorReportClone")
       suspend fun inspectorReportCloneRequest(
           @Field("sessionToken") sessionToken: String,
           @Field("requestId") requestId: String,
           @Field("customerSign") customerSign: String,
           @Field("inspectorSign") inspectorSign: String,
           @Field("timeTake") timeTake: String,
           @Field("questionJson") questionJson: String,
           @Field("graphBase64") graphBase64: String,
           @Field("flowTestTableValues") flowTestTableValues: String,
           @FieldMap additionalFields: HashMap<String, String>
       ): Response<InpectorReportClonResponse>  */



    @FormUrlEncoded
    @POST("createPdf")
    suspend fun inspectorReportCloneRequest(
        @Field("sessionToken") sessionToken: String,
        @Field("requestId") requestId: String,
        @Field("customerSign") customerSign: String,
        @Field("inspectorSign") inspectorSign: String,
        @Field("questionJson") questionJson: String,
        @Field("flowTestTableValues") flowTestTableValues: String,
        @Field("graphBase64") graphBase64: String,
        @Field("timeTake") timeTake: String,
        @Field("winterization") winterization: String,
        @Field("fireExtinguisher") fireExtinguisher: String,
        @Field("systemLocationText") systemLocationText: String,
        @Field("chart1") chart1: String,
        @Field("chart2") chart2: String,
        @FieldMap additionalFields: HashMap<String, String>
    ): Response<InpectorReportClonResponse>


    @FormUrlEncoded
    @POST("clockInList")
    suspend fun clockInListRequest(
        @Field("sessionToken") sessionToken: String,
        @Field("lat") lat: String,
        @Field("long") long: String,
    ): Response<ClockInListResponse>

    @FormUrlEncoded
    @POST("clockIn")
    suspend fun switchClockInRequest(
        @Field("sessionToken") sessionToken: String,
        @Field("lat") lat: String,
        @Field("long") long: String,
        @Field("project") project: String,
    ): Response<SwitchClockInResponse>

    @FormUrlEncoded
    @POST("applyLeave")
    suspend fun applyLeaveRequest(
        @Field("sessionToken") sessionToken: String,
        @Field("leaveDates") leaveDates: String,
        @Field("reason") reason: String,
        @Field("type") type: String,
    ): retrofit2.Response<ApplyLeavesResponse>
//            Response<ApplyLeavesResponse>

    @FormUrlEncoded
    @POST("clockInList")
    suspend fun clockInListScreenRequest(
        @Field("sessionToken") sessionToken: String,
        @Field("lat") lat: String,
        @Field("long") long: String,
        @Field("type") type: String,
    ): Response<ClockInListScreenResponse>


    @FormUrlEncoded
    @POST("dayClockIn")
    suspend fun dayClockInRequest(
        @Field("sessionToken") sessionToken: String,
        @Field("lat") lat: String,
        @Field("long") long: String,
        @Field("image") image: String,
        @Field("injured") injured: String,
    ): Response<DayClockInResponse>


    @POST("dashboard")
    suspend fun addRequestInspection(
        @Query("sessionToken") sessionToken: String,
        @Query("filterByDate") filterByDate: String
    ): InspectionResponseData


    @FormUrlEncoded
    @POST("notifications")
    suspend fun notificationsInspectionRequest(
        @Field("sessionToken") sessionToken: String,
    ): InspectionNotificationResponse

    @FormUrlEncoded
    @POST("uploadImage")
    suspend fun uploadImageRequest(
        @Field("image") image: String,
    ):  Response<UploadResponse>

    @FormUrlEncoded
    @POST("installation/addAssignedRequest")
    suspend fun addAssignedRequest(
        @Field("sessionToken") sessionToken: String,
        @Field("propertyName") propertyName: String,
        @Field("address") address: String,
        @Field("date") date:String,
        @Field("phoneNumber")  phoneNumber:String,
        @Field("email") email:String,
        @Field("city") city:String,
        @Field("state") state:String,
        @Field("postalCode") postalCode:String,
        @Field("assignedTime") assignedTime:String
    ): Response<InstallationAddAssignedResponse>



    @GET("installation/assignedRequest")
    suspend fun installationAssignedRequest(
        @Query("sessionToken") sessionToken: String,
    ): InstallationAssignedResponse


    @GET("emergency/request")
    suspend fun emergencyRequest(
        @Query("sessionToken") sessionToken: String,
    ): EmergencyRequestResponse


  @GET("emergency/delete")
    suspend fun emergencydeleteRequest(
        @Query("sessionToken") sessionToken: String,
        @Query("serviceId") serviceId: String,
    ): DeletedEmergencyRequestResponse


    @GET("leaves")
    suspend fun leavesRequest(
        @Query("sessionToken") sessionToken: String,
    ): GetLeavesResponse




//    type:2     *
//    buildingName:hgfhgf *
//    adddressLine1:dfgdfg   ---------
//    fullName:gfhg  ---------
//    phoneNumber:9876567876   ---------
//    inspectionType:1st Quarterly   ---------
//    postalCode:676   ---------
//    emailAddress:mailto:ss@gmail.com   ---------
//    city:dfgdf   ---------
//    state:gdfg   ---------
//    assignTime: 13:12   ---------
//    sessionToken: deG5sLuYFb  ---------
//    billTo: gfhg   ---------
//    assignDate: 2024-11-07   ---------
    @FormUrlEncoded
    @POST("inspectorAddReport")
    suspend fun addInspectionDetailList(
        @Field("sessionToken") sessionToken: String,
        @Field("fullName") fullName: String,
        @Field("emailAddress") emailAddress: String,
        @Field("phoneNumber") phoneNumber: String,
        @Field("adddressLine1") adddressLine1: String,
        @Field("billTo") billTo: String,
        @Field("city") city: String,
        @Field("state") state: String,
        @Field("postalCode") postalCode: String,
        @Field("assignDate") assignDate: String,
        @Field("assignTime") assignTime: String,
        @Field("inspectionType") inspectionType: String,
        @Field("type") type: String,
        @Field("buildingName") buildingName: String,

    ): Response<IspectionDetailListResponse>



    @FormUrlEncoded
    @POST("addExpense")
    suspend fun addExpenseRequest(
        @Field("sessionToken") sessionToken: String,
        @Field("title") title: String,
        @Field("image") image: String,
        @Field("category") category: String,
        @Field("price") price: String,
        @Field("projectId") projectId: String,
        @Field("reimbursement") reimbursement: String,
    ): Response<AddExpenseResponse>

    @FormUrlEncoded
    @POST("meetingUpdate")
    suspend fun meetingUpdateRequest(
        @Field("id") id: String,
        @Field("sessionToken") sessionToken: String,
        @Field("image") image: String
    ): Response<MeetingUpdateResponse>

    @FormUrlEncoded
    @POST("weekTimeSheet")
    suspend fun weekTimeSheetRequest(
        @Field("sessionToken") sessionToken: String,
        @Field("weekData") weekData: String
    ): Response<WeekTimeSheetResponse>

    @FormUrlEncoded
    @POST("chageRequestStatus")
    suspend fun chageRequestStatusRequest(
        @Field("sessionToken") sessionToken: String,
        @Field("type") type: String,
        @Field("requestId") requestId: String
    ): Response<AcceptRejectResponse>

 @FormUrlEncoded
    @POST("onRoute")
    suspend fun onRouteRequest(
        @Field("sessionToken") sessionToken: String,
        @Field("requestId") requestId: String
    ): Response<OnRouteResponse>


    @GET("checkRoute")
    suspend fun checkRouteRequest(
        @Query("sessionToken") sessionToken: String,
        @Query("requestId") requestId: String
    ): Response<CheckRouteResponse>

    @FormUrlEncoded
    @POST("respondMeetingReqest")
    suspend fun respondMeetingRequest(
        @Field("sessionToken") sessionToken: String,
        @Field("meetingId") meetingId: String,
        @Field("status") status: String
    ): Response<RespondMeetingRequestResponse>

    @FormUrlEncoded
    @POST("createProject")
    suspend fun createProjectRequest(
        @Field("sessionToken") sessionToken: String,
        @Field("name") name: String,
        @Field("contractorName") contractorName: String,
        @Field("lat") lat: String,
        @Field("long") long: String,
        @Field("address") address: String,
        @Field("city") city: String,
        @Field("state") state: String,
        @Field("postalCode") postalCode: String
    ): Response<CreateProjectResponse>

    @GET("homepage")
    suspend fun homeRequest(
        @Query("sessionToken") sessionToken: String,
        @Query("filterDate") filterDate: String
    ): HomeResponse

   @GET("safetyMeeting")
    suspend fun safetyMeetingRequest(
        @Query("sessionToken") sessionToken: String,
    ): SafetyMeetingsResponse


    @GET("questionclone")
    suspend fun questionListRequest(
        @Query("requestId") requestId: String,
    ): QuestionsResponse

 /*   @GET("questionclone")
    suspend fun questionListRequest(
        @Query("requestId") requestId: String,
        ): QuestionsListResponse*/

    @FormUrlEncoded
    @POST("addSystemFromReport")
    suspend fun addSystemFromReportRequest(
        @Field("requestId") requestId: String,
        @Field("systemId") systemId: String,
        @Field("systemDescription") systemDescription: String,
        @Field("assignedQuestion") assignedQuestion: String,
    ): AddSystemFromReportResponse

//    @GET("questionList")
//    suspend fun questionListRequest(): QuestionsListResponse

    @GET("pendingRequest")
    suspend fun pendingRequest(
        @Query("sessionToken") sessionToken: String,
    ): PendingRequestResponse

    @GET("completedRequest")
    suspend fun completedRequest(
        @Query("sessionToken") sessionToken: String,
    ): CompletedRequestsResponse

    @GET("schedules")
    suspend fun schedulesRequest(
        @Query("sessionToken") sessionToken: String,
        @Query("filterDate") filterDate: String
    ): ScheduleResponse

    @GET("chat/notes")
    suspend fun chatNotes(
        @Query("sessionToken") sessionToken: String,
    ): ChatNotesResponse


    @FormUrlEncoded
    @POST("feedback")
    suspend fun feedbackRequest(
        @Field("sessionToken") sessionToken: String,
        @Field("note") note: String,
        @Field("subject") subject: String,
    ): Response<FeedBackResponse>

    @FormUrlEncoded
    @POST("chat/messages/add")
    suspend fun addMessageRequest(
        @Field("sessionToken") sessionToken: String,
        @Field("userNoteId") userNoteId: String,
        @Field("text") text: String,
    ): Response<AddChatResponse>

    @GET("chat/messages")
    suspend fun chatMessages(
        @Query("sessionToken") sessionToken: String,
        @Query("userNoteId") userNoteId: String,
    ): ChatMessageResponse

    @GET("installation/viewPdf")
    suspend fun installationViewPdfRequest(
        @Query("tok") tok: String,
    ): InstallationPDFResponse

    @FormUrlEncoded
    @POST("dashboardInspection")
    suspend fun dashboardRequest(
        @Field("sessionToken") sessionToken: String,
        @Field("filterByDate") filterByDate: String,
        ): DashboardResponse

    @GET("userProfile")
    suspend fun profileRequest(
        @Query("sessionToken") sessionToken: String,
    ): ProfileResponse

    @FormUrlEncoded
    @POST("resetPassword")
    suspend fun resetPasswordRequest(
        @Field("sessionToken") sessionToken: String,
        @Field("password") password: String,
    ): ResetPasswordResponse

    @GET("organizations")
    suspend fun organizationsRequest(
        @Query("sessionToken") sessionToken: String,
    ): OrganizationResponse

    @FormUrlEncoded
    @POST("addOrganization")
    suspend fun addOrganizationRequest(
        @Field("phoneNumber") phoneNumber: String,
        @Field("organizationName") organizationName: String,
        @Field("emailAddress") emailAddress: String,
        @Field("fullName") fullName: String,
    ): AddOrganizationResponse

    @GET("userAddress")
    suspend fun addressRequest(
        @Query("sessionToken") sessionToken: String,
        @Query("organizationsId") organizationId: String,
    ): AddressResponse

    @FormUrlEncoded
    @POST("addAddress")
    suspend fun addAddressRequest(
        @Field("addressLine") addressLine: String,
        @Field("organizationsId") organizationsId: String,
        @Field("city") city: String,
        @Field("postalCode") postalCode: String,
        @Field("state") state: String,
    ): AddAddressResponse

    @GET("building")
    suspend fun buildingRequest(
        @Query("sessionToken") sessionToken: String,
        @Query("addressId") addressId: String,
    ): BuildingResponse

    @FormUrlEncoded
    @POST("addBuilding")
    suspend fun addBuildingRequest(
        @Field("addressId") addressId: String,
        @Field("name") name: String,
    ): AddBuildingResponse

    @FormUrlEncoded
    @POST("addRequestSystem")
    suspend fun addRequestRequest(
        @Field("organizationId") organizationId: String,
        @Field("addressId") addressId: String,
        @Field("inspectionType") inspectionType: String,
        @Field("assignTime") assignTime: String,
        @Field("sessionToken") sessionToken: String,
        @Field("assignDate") assignDate: String,
        @Field("buildingId") buildingId: String,
        @Field("billTo") billTo: String,
        @Field("notes") notes: String,
    ): AddRequestResponse

    @GET("systemId")
    suspend fun systemManagementRequest(
        @Query("sessionToken") sessionToken: String,
        @Query("buildingId") buildingId: String,
    ): SystemManagementResponse

    @FormUrlEncoded
    @POST("addSystemManagement")
    suspend fun addSystemRequest(
        @Field("systemDescription") systemDescription: String,
        @Field("assignedQuestion") assignedQuestion: String,
        @Field("addressId") addressId: String,
        @Field("systemId") systemId: String,
    ): AddSystemResponse


    /*

        @FormUrlEncoded
        @POST("forgotPasssword")
        suspend fun forgotPassword(
            @Field("email") email: String
        ): retrofit2.Response<RegisterResponse>



        @POST("register")
        @Multipart
        suspend fun registerRequest(
            @Part image:MultipartBody.Part,
            @Part("email") email: RequestBody,
            @Part("password") password: RequestBody,
            @Part("role") role: RequestBody,
            @Part("name") name: RequestBody,
            @Part("address") address: RequestBody,
            @Part("date_of_birth") DOB: RequestBody,
            @Part("latitude") latitude: RequestBody,
            @Part("longitude") longitude: RequestBody,
            @Part("country") country: RequestBody,
            @Part("asti") asti: RequestBody,
            @Part("driver_licence") driverLicence: RequestBody,
            @Part("monetary_status") monetaryStatus: RequestBody,
            @Part("terms_and_condition") termAndCondi: RequestBody,
            @Part("mobile_number") mobile: RequestBody,
            @Part("device_token") device_token: RequestBody,
        ): retrofit2.Response<RegisterResponse>

        @POST("auth/updateImage")
        @Multipart
        suspend fun uploadImage(@Part image: MultipartBody.Part) : Response<LoginResponse>


        @FormUrlEncoded
        @POST("verifyOtp")
        suspend fun verificationOTP(
            @Field("email") email: String,
            @Field("otp") otp: Int
        ): retrofit2.Response<RegisterResponse>

        @FormUrlEncoded
        @POST("resetPassword")
        suspend fun resetPassword(
            @Field("email") email: String,
            @Field("password") pass: String
        ): retrofit2.Response<RegisterResponse>


        @GET("auth/homeScreen")
        suspend fun homeScreen(
        ): retrofit2.Response<HomeResponse>

        @GET("auth/user/{id}")
        suspend fun getUserDetail(
            @Path("id") id:String)
        : retrofit2.Response<UserResponse>

        @FormUrlEncoded
        @POST("auth/editProfile")
        suspend fun editProfile(
            @Field("name") name: String,
            @Field("email") email: String,
            @Field("address") address: String,
            @Field("dob") dob: String,
            @Field("license") license: String,
            @Field("wwc_number") wcnumber: String,
        ): retrofit2.Response<RegisterResponse>


        @FormUrlEncoded
        @POST("auth/filterCoach")
        suspend fun filterCoach(
            @Field("name") name: String,
            @Field("type") type: String,
            @Field("distance") distance: String,
            @Field("date") date: String,
            @Field("atsi") atsi: String,
            @Field("slots") slots: String
        ): retrofit2.Response<FilterCoachResponse>

        @GET("auth/favorite")
        suspend fun favourite(
        ): FavouriteResponse

        @GET("auth/viewAppointment")
        suspend fun myRequest(
        ): MyRequestResponse

        @POST("auth/cancelBooking/{status}")
        suspend fun cancelBooking(
            @Path("status") status: Int,
        ): CancelBookingResponse

        @POST("auth/confirmBooking/{status}")
        suspend fun confirmBooking(
            @Path("status") status: Int,
        ): ConfirmBookingResponse

        @POST("auth/acceptBooking/{status}")
        suspend fun acceptBooking(
            @Path("status") status: Int,
        ): AcceptBookingResponse

        @POST("auth/declineBooking/{status}")
        suspend fun declinedBooking(
            @Path("status") status: Int,
        ): DeclinedBookingResponse

        @FormUrlEncoded
        @POST("auth/viewCoachAppointment")
        suspend fun viewAppointmrntForCoach(
            @Field("name") name: String,
            @Field("dates") dates: String,
        ): ViewAppointmentForCoachResponse

        @FormUrlEncoded
        @POST("auth/changeStatus")
        suspend fun changeStatus(
            @Field("status") status: Int,
        ): ChangeStatusResponse

        @GET("auth/completedAppointment")
        suspend fun completedRequest(
        ): CompletedAppointmentResponse


        @GET("auth/allCoach")
        suspend fun allCoachRequest(
        ): AllCoachResponse

        @FormUrlEncoded
        @POST("auth/addfavorite")
        suspend fun addfavoriteRequest(
            @Field("coach_id") coach_id: Int,
        ): AddToFavouriteResponse

        @GET("auth/coachDetails")
        suspend fun coachDetailsRequest(
            @Query("id") id: Int,
        ): CoachDetailsResponse

        @FormUrlEncoded
        @POST("auth/addRating")
        suspend fun addRatingRequest(
            @Field("given_by") given_by: String,
            @Field("given_to") given_to: String,
            @Field("rating") rating: String,
            @Field("comment") comment: String,
            @Field("slot_id") slot_id: String,
            ): RatingResponse

        @POST("logout")
        suspend fun logoutRequest(
        ): LogoutResponse

        @FormUrlEncoded
        @POST("createNewPassword")
        suspend fun createNewPasswordRequest(
            @Field("old_password") old_password: String,
            @Field("new_password") new_password: String,
        ): retrofit2.Response<CreateNewPasswordResponse>


        @GET("auth/notification")
        suspend fun notification(
        ): NotificationResponse

        @GET("auth/viewTickets")
        suspend fun viewtickets(
        ): ViewTicketsResponse

        @GET("auth/termsAndCondition")
        suspend fun termsandconditions(
        ): TermsAndConditionsResponse

        @GET("auth/aboutUs")
        suspend fun aboutus(
        ): AboutUsResponse

        @GET("auth/privacyPolicy")
        suspend fun privacypolicy(
        ): PrivacyPolicyResponse

        @GET("auth/viewDateAndTimeSlots/{status}")
        suspend fun viewandtimeSlots(
            @Path("status") status: Int,
        ): ViewDateTimeSlotsResponse

        @GET("auth/viewMySlots")
        suspend fun viewMySlotsForCoach(
        ): ViewMySlotsForCoachResponse

        @FormUrlEncoded
        @POST("auth/updateAvailability")
        suspend fun updateAvailability(
            @Field("slots") name: String,
            @Field("dates") dates: String,
        ): UpdateAvailabilityResponse

        @POST("auth/faqs")
        suspend fun faqsRequest(
        ): FAQSResponse


        @FormUrlEncoded
        @POST("auth/bookAppointment")
        suspend fun bookAppointment(
            @Field("coach_id") coach_id: String,
            @Field("slot_id") slot_id: String,
            @Field("appointment_date")appointment_date:String
        ): BookAppointmentResponse

        @FormUrlEncoded
        @POST("auth/addQuery")
        suspend fun addQuery(
            @Field("subject") subject: String,
        ): AddQueryResponse


    */

}
