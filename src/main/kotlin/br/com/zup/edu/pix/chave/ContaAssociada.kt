package br.com.zup.edu.pix.chave

import javax.persistence.Column
import javax.persistence.Embeddable
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size
import kotlin.math.min

@Embeddable
class ContaAssociada(

    @field:NotBlank
    @Column(nullable = false)
    val instituicao:String,

    @field:NotBlank
    @Column(nullable = false)
    val nomeDoTitulo: String,

    @field:NotBlank
    @field:Pattern(regexp = "[0-9]{11}")
    @Column(length= 11,  nullable = false)
    val cpfDoTitular:String,

    @field:NotBlank
    @field:Size(min= 4, max = 4)
    @Column(length= 4,nullable = false)
    val agencia: String,

    @field:NotBlank
    @field:Size(min= 6, max = 6)
    @Column(length= 6, nullable = false)
    val numeroDaConta:String
){
    companion object{
        public val ITAU_UNIBANCO_ISPB:String = "60701190"
    }
}



