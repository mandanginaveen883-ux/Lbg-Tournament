package com.example.data.model

enum class TournamentType {
    FREE,
    PAID
}

enum class MatchMode {
    SOLO,
    DUO,
    SQUAD
}

enum class MatchStatus {
    UPCOMING,
    REGISTRATION_OPEN,
    REGISTRATION_FULL,
    LIVE,
    COMPLETED,
    CANCELLED
}

enum class RegistrationStatus {
    AWAITING_PAYMENT,
    PAYMENT_SUBMITTED,
    CONFIRMED,
    REJECTED,
    EXPIRED,
    REFUNDED
}

enum class PaymentStatus {
    FREE_ENTRY,
    UNPAID,
    VERIFICATION_PENDING,
    VERIFIED_PAID,
    REJECTED,
    REFUNDED
}
