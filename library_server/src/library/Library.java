package library;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Library {
	
	Map<String, Book> books;

	public Library() {
		 this.books = new HashMap<String, Book>();
		 this.loadDefBooks();
	}
	
	private void loadDefBooks() {
		this.addBook("978-8478884452", "La náusea", "Jean-Paul Sartre",8.95, 5);
		this.addBook("847-8474564232", "1984", "Georges Orwell",11.75, 4);
		this.addBook("452-8478332452", "Rebelión en la Granja", "Georges Orwell",7, 4);
		this.addBook("898-5678884452", "Harry Potter y la piedra filosofal", "J.K. Rowling",11.50, 10);
		this.addBook("432-8823584452", "Harry Potter y la cámara secreta", "J.K. Rowling",12.35, 8);
		this.addBook("123-8478885566", "Harry Potter y el priosionero de Azkabán", "J.K. Rowling",13, 7);
	}	
	
	private String title2isbn(String title) {
		for(String isbn : this.books.keySet()) {
			Book book = this.books.get(isbn);
			if(book.getTitle().equals(title)) {
				return isbn;
			}
		}
		return null;
	}
	
	private Book getBook(String isbn) {
		if(this.books.containsKey(isbn) && isbn != null) {
			return this.books.get(isbn);
		}else {
			return null;
		}
	}
	
	private Book buyBook(String isbn) {
		Book the_book = this.getBook(isbn);
		if(the_book.sell(1)) {
			this.books.put(isbn, the_book);
			return the_book;
		}else {
			return null;
		}
	}
	
	private boolean removeBook(String isbn) {
		if(this.books.get(isbn) != null) {
			this.books.remove(isbn);
			return true;
		}
		return false;		
	}
		
	public void addBook(String isbn, String title, String author, double price) {
		this.addBook(isbn, title, author, price, 1);
	}
	
	public void addBook(String isbn, String title, String author, double price, int copies) {
		if(this.books.containsKey(isbn)) {
			Book the_book = this.books.get(isbn);
			the_book.addCopies(copies);
			this.books.put(isbn, the_book);
		}else {
			books.put(isbn,new Book(isbn, title, author, price, copies));
		}
	}
	
	public Book getBookByISBN(String isbn) {
		return this.getBook(isbn);
	}
	
	public Book getBookByTitle(String title) {	
		return this.getBook(this.title2isbn(title));
	}	
	
	public ArrayList<Book> getBooksByAuthor(String author) {
		ArrayList<Book> books_by_auth = new ArrayList<Book>();
		for(String isbn : this.books.keySet()) {
			Book current_book = this.books.get(isbn);
			if(current_book.getAuthor().equals(author)) {
				books_by_auth.add(current_book);
			}
		}
		return books_by_auth;
	}

	public Book buyBookByISBN(String isbn) {
		return this.buyBook(isbn);		
	}
	
	public Book buyBookByTitle(String title) {
		return this.buyBookByISBN(this.title2isbn(title));
	}
	
	public boolean removeBookByISBN(String isbn) {
		return this.removeBook(isbn);		
	}
	
	public boolean removeBookByTitle(String title) {
		return this.removeBook(this.title2isbn(title));
	}
}
