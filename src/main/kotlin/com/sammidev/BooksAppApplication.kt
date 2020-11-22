package com.sammidev

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Repository
import org.springframework.web.bind.annotation.*
import java.util.concurrent.atomic.AtomicLong
import javax.persistence.*
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@SpringBootApplication
class BooksAppApplication

fun main(args: Array<String>) {
	runApplication<BooksAppApplication>(*args) {
		setBannerMode(Banner.Mode.OFF)
	}
}
// entity
@Entity(name = "books")
data class Book(
		@Id @GeneratedValue(
				strategy = GenerationType.IDENTITY) val id : Long = 0,
		@Column(name = "author") @field:NotBlank @get: NotBlank val author: String = "",
		@Column(name = "year") @field:NotBlank @get: NotBlank val year: Int = 0,
		@Column(name = "title") @field:NotBlank @get: NotBlank val title : String = "",
		@Column(name = "published") @field:NotBlank @get: NotBlank val published: String = "",
		@Column(name = "publisher") @field:NotBlank @get: NotBlank val publisher: String = "",
)

// testing
data class Greeting(val id: Long, val content: String)

@Repository
interface BookRepository : JpaRepository<Book, Long>

@RestController
@RequestMapping("/api")
class BookController(@Autowired private val bookRepository: BookRepository) {

	val counter = AtomicLong()
	@GetMapping("/health")
	fun greeting(@RequestParam(value = "name", defaultValue = "sammidev") name: String) =
			Greeting(counter.incrementAndGet(), "Helloo, $name")

	// get all books
	@GetMapping("/books")
	fun getAllBooks() : List<Book> = bookRepository.findAll()

	// create a book
	@PostMapping("/books")
	fun createBook(@Valid @RequestBody book: Book) : Book = bookRepository.save(book)


	// get single book
	@GetMapping("books/{bookID}")
	fun getBookById(@PathVariable bookID: Long) : ResponseEntity<Book> = bookRepository.findById(bookID)
			.map { ResponseEntity.ok(it) }
			.orElse(ResponseEntity.notFound().build())

	// update a book
	@PutMapping("/books/{bookID}")
	fun updateBook(@PathVariable bookID: Long, @Valid @RequestBody updatedBook: Book) : ResponseEntity<Book> =
			bookRepository.findById(bookID)
					.map { val newBook = it.copy(
							author = updatedBook.author,
							year = updatedBook.year,
							title = updatedBook.title,
							published = updatedBook.published,
							publisher = updatedBook.publisher)
						ResponseEntity.ok().body(
								bookRepository.save(newBook))
					}.orElse(
							ResponseEntity.notFound().build())

	// delete book
	@DeleteMapping("/books/{bookID}")
	fun deleteBook(@PathVariable bookID: Long) : ResponseEntity<Void> =
			bookRepository.findById(bookID)
					.map { bookRepository.delete(it)
						ResponseEntity<Void>(HttpStatus.OK)
					}
					.orElse(
							ResponseEntity.notFound().build())
}
