package library;

public class Book{
	private String isbn;
	private String author;
	private int copies;
	private double price;
	private String title;
	
	public Book(String isbn, String title, String author, double price, int copies) {
		this.isbn = isbn;
		this.title = title;
		this.author = author;
		this.price = price;
		this.copies = copies;
	}
	
	public String getIsbn() {
		return isbn;
	}
	
	public String getAuthor() {
		return author;
	}
	
	public String getTitle() {
		return this.title;
	}	

	public int getCopies() {
		return this.copies;
	}
		
	public int addCopies(int n) {
		this.copies += n;		
		return this.copies;
	}
	
	public boolean sell(int n) {
		if((this.copies - n) > 0) {
			this.copies -= n;
			return true;
		}
		return false;
	}
	
	public boolean isAvailable() {
		return this.copies > 0;
	}
	
	@Override
	public String toString() {
		return "isbn:" + isbn 
				+ " | titulo:" + title 
				+ " | autor:" + author 
				+ " | precio:" + price + "€" 
				+ " | no copias:" + copies;
	}

}
