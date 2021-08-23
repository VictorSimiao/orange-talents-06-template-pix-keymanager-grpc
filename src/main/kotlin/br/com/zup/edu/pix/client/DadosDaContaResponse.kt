package br.com.zup.edu.pix.client
import br.com.zup.edu.pix.chave.ContaAssociada

data class DadosDaContaResponse(
    val tipo: String,
    val instituicao: InstituicaoResponse,
    val agencia: String,
    val numero: String,
    val titular: TitularResponse

) {
    fun toModel():ContaAssociada{
        return ContaAssociada(
            instituicao = this.instituicao.nome,
            nomeDoTitulo = this.titular.nome,
            cpfDoTitular = this.titular.cpf,
            agencia = agencia,
            numeroDaConta = this.numero
        )
    }

}

data class TitularResponse(val nome: String, val cpf: String)
data class InstituicaoResponse(val nome: String, val ispb: String)