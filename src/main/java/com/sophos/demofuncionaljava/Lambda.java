package com.sophos.demofuncionaljava;

import java.util.*;
import java.util.function.*;

public class Lambda {

	public static void main(String[] args) {
		final Lambda lambda = new Lambda();

		// Funciones Lambda
		lambda.ejemplosLambda();

		// Composición de funciones
		lambda.ejemplosComposicion();

	}

	void ejemplosLambda() {
		System.out.println("Function");
		Function<String, Integer> convertir = s -> Integer.parseInt(s);
		System.out.println(convertir.apply("12345"));
		System.out.println();

		System.out.println("Función con tipo definido -> Integer");
		IntFunction<Integer> elevarAlCuadrado = i -> (int)Math.pow(i, 2);
		System.out.println(elevarAlCuadrado.apply(4));
		System.out.println();


		System.out.println("BiFunction");
		BiFunction<String, Integer, List<Object>> agrupar = (s, i) -> List.of(s, i);
		System.out.println(agrupar.apply("Hola", 10));
		System.out.println();

		System.out.println("Operator");
		UnaryOperator<Integer> duplicar = i -> i * 2;
		System.out.println(duplicar.apply(5));
		IntUnaryOperator triplicar = i -> i * 3;
		System.out.println(triplicar.applyAsInt(5));
		System.out.println();

		System.out.println("Consumer");
		Consumer<String> imprimir = s -> System.out.println(s);
		imprimir.accept("Hola mundo!");
		System.out.println();

		System.out.println("Supplier");
		Supplier<UUID> rand = () -> UUID.randomUUID();
		System.out.println(rand.get());
		System.out.println();

		System.out.println("Predicate");
		Predicate<String> validar = s -> s.contains("e");
		System.out.println(validar.test("Ricardo"));
	}

	void ejemplosComposicion() {
		// Primera función
		Function<String, List<String>> partir = s -> {
			final List<String> letras = new ArrayList<>();
			for(int i = 0 ; i < s.length() ; i++) {
				letras.add(String.valueOf(s.charAt(i)));
			}
			return letras;
		};
		System.out.println("Primera función");
		System.out.println(partir.apply("Hola mundo!"));
		System.out.println();

		Function<List<String>, Map<String, Integer>> contar = letras -> {
			final Map<String, Integer> frecuencia = new TreeMap<>();
			for(String l : letras) {
				frecuencia.compute(l, (k, v) -> v == null ? 1 : v + 1);
			}
			return frecuencia;
		};
		System.out.println("Segunda función");
		System.out.println(contar.apply(List.of("s", "o", "p", "h", "o", "s")));
		System.out.println();

		Function<Map<String, Integer>, String> mostrar = m -> {
			StringBuilder sb = new StringBuilder("Frecuencias\n");
			m.forEach((clave, valor) -> sb.append(clave).append(": ").append(valor).append("\n"));
			return sb.toString();
		};
		System.out.println("Tercera función");
		System.out.println(mostrar.apply(Map.of("R", 2, "J", 1, "B", 1)));
		System.out.println();

		Function<String, String> composicion = contar.compose(partir).andThen(mostrar);
		System.out.println("Composición de funciones");
		System.out.println(composicion.apply("Hola, soy Ricardo Ramírez"));
		System.out.println();

	}

}
