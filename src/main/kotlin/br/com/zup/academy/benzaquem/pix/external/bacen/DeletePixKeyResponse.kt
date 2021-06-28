package br.com.zup.academy.benzaquem.pix.external.bacen

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DeletePixKeyResponse(
    val key: String,
    val participant: String,
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
    val deletedAt: LocalDateTime
) {
    override fun toString(): String {
        DateTimeFormatter.ISO_DATE_TIME
        return "DeletePixKeyResponse(key='$key', participant='$participant', deletedAt=$deletedAt)"
    }
}
