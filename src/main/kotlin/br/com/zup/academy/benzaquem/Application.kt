package br.com.zup.academy.benzaquem

import io.micronaut.runtime.Micronaut.*
fun main(args: Array<String>) {
	build()
	    .args(*args)
		.packages("br.com.zup.academy.benzaquem")
		.start()
}

