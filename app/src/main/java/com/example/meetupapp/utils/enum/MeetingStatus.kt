package com.example.meetupapp.utils.enum

enum class MeetingStatus(val status: String) {
    IN_PROGRESS("in progress"),
    ACCEPTED("accepted"),
    REJECTED("rejected");

    companion object {
        fun changeMeetingStatus(newStatus: MeetingStatus) {
        }
    }
}
