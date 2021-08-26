package br.com.zup.edu.pix.client.bcb



import br.com.zup.edu.pix.client.bcb.dtos.CreatePixKeyRequest
import br.com.zup.edu.pix.client.bcb.dtos.CreatePixKeyResponse
import br.com.zup.edu.pix.client.bcb.dtos.DeletePixKeyRequest
import br.com.zup.edu.pix.client.bcb.dtos.DeletePixKeyResponse
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client


@Client("\${bcb.pix.url}")
interface BancoCentralClient {

    @Post(
        "/api/v1/pix/keys",
        produces = [MediaType.APPLICATION_XML],
        consumes = [MediaType.APPLICATION_XML]
    )
   fun registraChave(@Body request: CreatePixKeyRequest):HttpResponse<CreatePixKeyResponse>

    @Delete("/api/v1/pix/keys/{key}",
        produces = [MediaType.APPLICATION_XML],
        consumes = [MediaType.APPLICATION_XML]
    )
    fun deletaChave(@PathVariable key: String, @Body request: DeletePixKeyRequest): HttpResponse<DeletePixKeyResponse>



}