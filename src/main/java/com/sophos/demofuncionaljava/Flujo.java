package com.sophos.demofuncionaljava;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Flujo {

	public static void main(String[] args) {
		Flujo flujos = new Flujo();

		flujos.crearStream();
		flujos.ejemplos();
		flujos.casosPracticos();
	}

	public void crearStream() {
		// Streams infinitos
		Stream.generate(UUID::randomUUID).limit(5).forEach(uuid -> mostrar.accept("Elemento del stream - Generate", uuid));
		Stream.iterate(1, n -> n < 5, n -> n + 1).forEach(n -> mostrar.accept("Elemento del stream - Iterate acotado", n));
		Stream.iterate(10, n -> n + 2).limit(5).forEach(n -> mostrar.accept("Elemento del stream - Iterate infinito", n));

		// Streams a partir de colecciones
		Stream.builder().add(1).add(2).add(3).build()
			.peek(i -> System.out.println("Antes del salto: " + i))
			.skip(2)
			.peek(i -> System.out.println("Luego del salto: " + i))
			.forEach(i -> mostrar.accept("Builder", i))
		;

		Stream.of("c", "b", "a").sorted().forEach(s -> mostrar.accept("Of ...", s));

		String[] textos = {"soy", "un", "arreglo", "de", "textos"};
		Stream.of(textos).skip(2).limit(2).forEach(s -> mostrar.accept("Of array", s));

		List<String> lista1 = List.of("1", "2", "3");
		lista1.stream().forEach(s -> mostrar.accept("stream()", s));

		List<String> lista2 = List.of("a", "b", "c");
		Stream.concat(lista1.stream(), lista2.stream()).forEach(s -> mostrar.accept("Concat", s));

		// Streams numéricos de tamaño específico o iteración sobre un rango
		mostrar.accept("IntStream", IntStream.rangeClosed(1, 10).boxed().collect(Collectors.toList()));
	}

	public void ejemplos() {
		// Palabra más larga
		String palabraMasLargaMax = List.of("uno", "dos", "tres", "cuatro", "cinco").parallelStream()
			.max(Comparator.comparing(String::length))
			.orElseThrow()
		;
		System.out.println("Palabra más larga (con max): " + palabraMasLargaMax);
		String palabraMasLargaRed = List.of("uno", "dos", "tres", "cuatro", "cinco").parallelStream()
			.reduce("", (s1, s2) -> s1.length() < s2.length() ? s2 : s1)
		;
		System.out.println("Palabra más larga (con reduce): " + palabraMasLargaRed);

		// Intersección de listas
		var lista1 = List.of(9,3,1,5,8,7,2,4,6,0);
		var lista2 = List.of(8,6,4);
		String interseccionListas = lista1.stream()
			.filter(lista2::contains)
			.sorted()
			.map(String::valueOf)
			.collect(Collectors.joining(", "))
		;
		System.out.println("Intersección de listas: " + interseccionListas);

		// Distinct
		List<Integer> distintos = new Random().ints(1, 10)
			.limit(10)
//			.peek(i -> System.out.print(i + " "))
			.distinct()
			.sorted()
			.boxed()
			.collect(Collectors.toList())
		;
		System.out.println("Elementos distintos: " + distintos);

		// Frecuencia de datos
		Map<Integer, Long> frecuenciaDatos = new Random().ints(10000, 0, 10)
			.boxed()
			.collect(
				Collectors.groupingBy(
					Function.identity(),
					Collectors.counting()
				)
			)
		;
		System.out.println("Frecuencia de datos: " + frecuenciaDatos);

		// Agrupar por rangos
		Map<Integer, List<Integer>> agruparPorRangos = IntStream.iterate(1, i -> i < 40, i -> i + 3)
			.boxed()
			.collect(Collectors.groupingBy(i -> i / 10 * 10))
		;
		System.out.println("Agrupar por rangos: " + agruparPorRangos);

		// Reducción (n * (n + 1)) / 2
		int sumatoria = IntStream.rangeClosed(0, 100)
			.reduce(0, (acumulado, valor) -> acumulado + valor)
		;
		System.out.println("Sumatoria de 1 a N: " + sumatoria);

		// Map-Reduce
		int mapReduceInt1 = Stream.of(1, 2, 3)
			.parallel()
			.reduce(
				10,
				(acumulado, valor) -> acumulado * valor,
				(combinado, valor) -> combinado + valor
			)
		;
		System.out.println("Map-Reduce con Reduce: " + mapReduceInt1);
		int mapReduceInt2 = Stream.of(1, 2, 3)
			.mapToInt(i -> i * 10)
			.sum()
		;
		System.out.println("Map-Reduce simplificado: " + mapReduceInt2);

		// Capitalize
		String cualquierTexto = "uN teXto   que QUERemos CAPITALIZAR";
		String capitalizado = Stream.ofNullable(cualquierTexto.strip().split("\\s+"))
			.flatMap(Stream::of)
			.map(word -> word.length() == 1 ? word.toUpperCase() : word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
			.collect(Collectors.joining(" "))
		;
		System.out.println("Capitalizar: " + capitalizado);

		// Frecuencia de números
		List<Integer> numeros = List.of(1,2,1,3,3,1,2,1,5,1,3);
		Map<Integer, Long> frecuencia = numeros.stream()
			.collect(
				Collectors.groupingBy(
					Function.identity(),
					Collectors.counting()
				)
			)
		;
		IntSummaryStatistics statistics = numeros.stream().mapToInt(i -> i).summaryStatistics();
		int min = statistics.getMin();
		int max = statistics.getMax();
		String formato = IntStream.rangeClosed(min, max)
			.mapToObj(i -> i + ": " + "*".repeat(frecuencia.getOrDefault(i, 0L).intValue()))
			.collect(Collectors.joining("\n"))
		;
		System.out.println("Frecuencia de números con formato: \n" + formato);
	}

	public void casosPracticos() {
		final List<Empleado> empleados = List.of(
			new Empleado("Empleado1", "Medellín", 30, 1000),
			new Empleado("Empleado2", "Bogotá", 23, 800),
			new Empleado("Empleado3", "Medellín", 28, 800),
			new Empleado("Empleado4", "Bogotá", 31, 1200),
			new Empleado("Empleado5", "Bogotá", 24, 900),
			new Empleado("Empleado6", "Medellín", 28, 1000),
			new Empleado("Empleado7", "Panamá", 45, 1200),
			new Empleado("Empleado8", "Bogotá", 38, 2000),
			new Empleado("Empleado9", "México", 36, 2500),
			new Empleado("Empleado0", "Medellín", 25, 800)
		);

		// Contar empleados
		System.out.println("Cantidad de empleados: " + empleados.stream().count());

		// Suma de salarios - Imperativo
		int totalSalarios = 0;
		for(Empleado empleado : empleados) {
			totalSalarios += empleado.getSalario();
		}
		System.out.println("Salarios totales (imperativo): " + totalSalarios);

		// Suma de salarios - Streams
		int salarioTotal1 = empleados.stream().mapToInt(Empleado::getSalario).sum();
		int salarioTotal2 = empleados.stream().collect(Collectors.summingInt(Empleado::getSalario));
		System.out.println("Salarios totales (mapReduce): " + salarioTotal1);
		System.out.println("Salarios totales (collect): " + salarioTotal2);

		// Aplanar listas de elementos, filtrar y particionar
		var lista1 = empleados.subList(0, 2);
		var lista2 = empleados.subList(5, 9);
		Map<Boolean, List<Integer>> mayores30 = Stream.of(lista1, lista2)
			.flatMap(l -> l.stream())
			.map(Empleado::getEdad)
			.collect(Collectors.partitioningBy(edad -> edad >= 30))
		;
		System.out.println("Particionar: " + mayores30);

		// Empleados con salario superior a 1000
		List<String> salario1000 = empleados.parallelStream()
			.filter(empleado -> empleado.getSalario() > 1000)
			.map(Empleado::getNombre)
			.collect(Collectors.toList())
		;
		System.out.println("Salarios superiores a: " + salario1000);

		// Agrupar por ciudad
		Map<String, List<Empleado>> empleadosCiudad = empleados.stream()
			.collect(Collectors.groupingBy(Empleado::getCiudad))
		;
		System.out.println("Empleados por ciudad: " + empleadosCiudad);

		// Agrupar empleados por ciudad y mapear sólo a los nombres
		Map<String, List<String>> empleadosCiudadNombre = empleados.parallelStream()
			.collect(
				Collectors.groupingBy(
					Empleado::getCiudad,
					Collectors.mapping(Empleado::getNombre, Collectors.toList())
				)
			)
		;
		System.out.println("Empleados por ciudad (sólo nombres): " + empleadosCiudadNombre);

		// Cantidad de empleados por ciudad
		Map<String, Long> cantidadPorCiudad = empleados.stream()
			.collect(
				Collectors.groupingBy(
					Empleado::getCiudad,
					Collectors.counting()
				)
			)
		;
		System.out.println("Cantidad de empleados por ciudad: " + cantidadPorCiudad);

		// Salario promedio de los empleados menores de 30 años que viven en Bogotá
		double salarioPromedioMenoresBogota = empleados.parallelStream()
			.filter(empleado -> empleado.getEdad() < 30 && empleado.getCiudad().equals("Bogotá"))
			.collect(Collectors.averagingInt(Empleado::getSalario))
		;
		System.out.println("Salario promedio de los menores de 30 en Bogotá: " + salarioPromedioMenoresBogota);

		// Estadísticas de los salarios de los empleados de Medellín
		Predicate<Empleado> soloMedellin = empleado -> empleado.getCiudad().equals("Medellín");
		ToIntFunction<Empleado> getSalario = Empleado::getSalario;
		IntSummaryStatistics statistics = empleados.parallelStream()
			.filter(soloMedellin)
			.mapToInt(getSalario)
			.summaryStatistics()
		;
		System.out.println("Empleados en Medellín: " + statistics.getCount());
		System.out.println("Máximo salario: " + statistics.getMax());
		System.out.println("Mínimo salario: " + statistics.getMin());
		System.out.println("Salario promedio: " + statistics.getAverage());
		System.out.println("Suma de salarios: " + statistics.getSum());

		// Salario máximo por ciudad, ordenado por ciudad
		final Map<String, Integer> maximos = empleados.parallelStream()
			.collect(
				Collectors.toMap(
					Empleado::getCiudad,
					Empleado::getSalario,
					Integer::max,
					TreeMap::new
				)
			)
		;
		System.out.println("Salario máximo por ciudad: " + maximos);

		// Empleados con salario superior a un límite por ciudad
		Map<String, List<String>> mejoresPagos = empleados.parallelStream()
			.collect(
				Collectors.groupingBy(
					Empleado::getCiudad,
					Collectors.filtering(
						empleado -> empleado.getSalario() > 1000,
						Collectors.mapping(Empleado::getNombre, Collectors.toList())
					)
				)
			)
		;
		System.out.println("Empleados con salario superior a 1000 por ciudad");
		mejoresPagos.forEach((ciudad, empleadosPorCiudad) -> System.out.println(ciudad + " ::: " + empleadosPorCiudad));

		// Algún empleado con salario superior a 2000
		boolean algunoSuperior2000 = empleados.parallelStream()
			.anyMatch(empleado -> empleado.getSalario() > 2000)
		;
		System.out.println("¿Algún empleado con salario superior a 2000? " + algunoSuperior2000);

		// Cualquier empleado con salario superior a 2000
		System.out.println("Cualquier empleado con salario superior a 2000");
		empleados.parallelStream()
			.filter(empleado -> empleado.getSalario() >= 2000)
			.map(Empleado::getNombre)
			.findAny()
			.ifPresent(System.out::println)
		;

		// Primer empleado con salario igual o superior a 1000
		System.out.println("Primer empleado con salario igual o superior a 1000");
		empleados.parallelStream()
			.filter(empleado -> empleado.getSalario() >= 1000)
			.map(Empleado::getNombre)
			.findFirst()
			.ifPresent(System.out::println)
		;

		// Asegurar que no pase algo
		boolean nadieSuperior3000 = empleados.parallelStream()
			.noneMatch(empleado -> empleado.getSalario() > 3000)
		;
		System.out.println("Nadie gana más de 3000: " + nadieSuperior3000);

		// Asegurar que pase algo
		boolean todosSuperior1000 = empleados.parallelStream()
			.allMatch(empleado -> empleado.getSalario() > 1000)
		;
		System.out.println("Todos por encima de 1000: " + todosSuperior1000);
	}


	static class Empleado {

		private String nombre;
		private String ciudad;
		private int edad;
		private int salario;

		public Empleado(String nombre, String ciudad, int edad, int salario) {
			this.nombre = nombre;
			this.ciudad = ciudad;
			this.edad = edad;
			this.salario = salario;
		}

		@Override
		public String toString() {
			return "Empleado{" +
				"nombre='" + nombre + '\'' +
				", ciudad='" + ciudad + '\'' +
				", edad=" + edad +
				", salario=" + salario +
			'}';
		}

		public String getNombre() {
			return nombre;
		}

		public void setNombre(String nombre) {
			this.nombre = nombre;
		}

		public String getCiudad() {
			return ciudad;
		}

		public void setCiudad(String ciudad) {
			this.ciudad = ciudad;
		}

		public int getEdad() {
			return edad;
		}

		public void setEdad(int edad) {
			this.edad = edad;
		}

		public int getSalario() {
			return salario;
		}

		public void setSalario(int salario) {
			this.salario = salario;
		}
	}

	private final BiConsumer<String, Object> mostrar = (titulo, salida) -> {
		System.out.println(titulo);
		System.out.println(salida);
		System.out.println();
	};

}
