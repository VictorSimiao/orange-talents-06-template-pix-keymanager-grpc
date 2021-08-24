package br.com.zup.edu.pix.exceptions

import java.lang.RuntimeException

class ClienteNaoEncontradoException(message:String?): RuntimeException(message) {
}