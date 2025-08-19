package com.LiterAlura.LiterAlura;

import com.LiterAlura.LiterAlura.api.dto.GBook;
import com.LiterAlura.LiterAlura.domain.Author;
import com.LiterAlura.LiterAlura.domain.Book;
import com.LiterAlura.LiterAlura.service.http.CatalogoLibros;
import com.LiterAlura.LiterAlura.service.http.HttpBookClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

@SpringBootApplication
public class LiterAluraApplication implements CommandLineRunner {

	private final CatalogoLibros catalog;

	@Autowired
	public LiterAluraApplication(CatalogoLibros catalog) {
		this.catalog = catalog;
	}

	public static void main(String[] args) {
		SpringApplication.run(LiterAluraApplication.class, args);
	}

	@Override
	public void run(String... args) {
		mostrarMenu();
	}

	/* =======================
       Menú principal en consola
       ======================= */
	private void mostrarMenu() {
		Scanner in = new Scanner(System.in);
		HttpBookClient http = new HttpBookClient();
		String opcion;

		while (true) {
			imprimirOpciones();
			System.out.print("Elige una opción: ");
			opcion = in.nextLine().trim();

			try {
				switch (opcion) {
					case "1" -> opcionBuscarYGuardar(in, http);
					case "2" -> opcionListarLibros();
					case "3" -> opcionListarAutores();
					case "4" -> opcionAutoresVivos(in);
					case "5" -> opcionLibrosPorIdioma(in);
					case "6" -> opcionTopDescargas();
					case "0" -> {
						System.out.println("¡Hasta luego! 👋");
						return;
					}
					default -> System.out.println("❌ Opción inválida. Intenta nuevamente.");
				}
			} catch (NumberFormatException | InputMismatchException e) {
				System.out.println("⚠️ Entrada inválida. Intenta nuevamente.");
			} catch (Exception e) {
				System.out.println("⚠️ Ocurrió un error: " + e.getMessage());
			}
			System.out.println();
		}
	}

	private void imprimirOpciones() {
		System.out.println("""
                
                === LiterAlura ===
                1) Buscar libro en API y guardar
                2) Listar libros registrados
                3) Listar autores registrados
                4) Listar autores vivos en un año
                5) Listar libros por idioma
                6) Top 10 por descargas
                0) Salir
                """);
	}

    /* =======================
       Opciones del menú
       ======================= */

	// 1) Buscar en Gutendex y GUARDAR el PRIMER resultado automáticamente
	private void opcionBuscarYGuardar(Scanner in, HttpBookClient http) {
		System.out.print("Texto a buscar (título/autor/tema): ");
		String query = in.nextLine().trim();
		if (query.isEmpty()) {
			System.out.println("La búsqueda no puede estar vacía.");
			return;
		}

		List<GBook> results = http.search(query);
		if (results.isEmpty()) {
			System.out.println("Sin resultados.");
			return;
		}

		// Tomamos el PRIMER resultado
		GBook first = results.get(0);

		String author = (first.authors() != null && !first.authors().isEmpty())
				? first.authors().get(0).name()
				: "desconocido";
		String language = (first.languages() != null && !first.languages().isEmpty())
				? first.languages().get(0)
				: "unknown";
		int downloads = first.downloadCount();

		// Guardamos en la BD usando tu servicio
		Book saved = catalog.saveFromApi(first);

		System.out.println("\n✅ Guardado (primer resultado):");
		System.out.printf("Título: %s%n", first.title());
		System.out.printf("Autor: %s%n", author);
		System.out.printf("Idioma: %s%n", language);
		System.out.printf("Descargas: %d%n", downloads);
	}


	// 2) Listar libros (desde BD)
	private void opcionListarLibros() {
		List<Book> books = catalog.listAllBooks();
		if (books.isEmpty()) {
			System.out.println("No hay libros registrados.");
			return;
		}
		// Usa el toString() de Book
		books.forEach(System.out::println);
	}

	// 3) Listar autores (desde BD)
	private void opcionListarAutores() {
		var authors = catalog.listAllAuthors();
		if (authors.isEmpty()) {
			System.out.println("No hay autores registrados.");
			return;
		}
		// Usa el toString() de Author
		authors.forEach(System.out::println);
	}

	// 4) Autores vivos en un año
	private void opcionAutoresVivos(Scanner in) {
		System.out.print("Año (ej. 1900): ");
		int year = Integer.parseInt(in.nextLine());
		var alive = catalog.authorsAliveIn(year);
		if (alive.isEmpty()) {
			System.out.println("No se encontraron autores vivos en " + year + ".");
			return;
		}
		alive.forEach(a -> System.out.println("- " + a.getName()));
	}

	// 5) Libros por idioma
	private void opcionLibrosPorIdioma(Scanner in) {
		var langs = catalog.listAllLanguages();
		if (langs.isEmpty()) {
			System.out.println("No hay idiomas registrados aún. Guarda libros primero.");
			return;
		}
		System.out.println("Idiomas disponibles: " + langs);
		System.out.print("Idioma a filtrar (ej. en, es, fr): ");
		String lang = in.nextLine().trim();

		var filtered = catalog.listByLanguage(lang);
		if (filtered.isEmpty()) {
			System.out.println("No hay libros para el idioma '" + lang + "'.");
			return;
		}
		filtered.forEach(b -> System.out.println("- " + b.getTitle()));
	}

	// 6) Top por descargas
	private void opcionTopDescargas() {
		var top = catalog.top10();
		if (top.isEmpty()) {
			System.out.println("No hay datos aún. Guarda libros primero.");
			return;
		}
		top.forEach(b -> System.out.printf("- %s (%d)%n", b.getTitle(), b.getDownloadCount()));
	}
}
