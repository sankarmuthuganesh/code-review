package RealTime.Author;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthorIdent {
	//--------------------THE BELOW DETAILS ARE AUTHOR IDENTITY GATHERED FROM COMMIT LOG--------------------------------//
		//Name of Author
		private String nameOfAuthor;
		//Email Address Of Author
		private String emailAddress;
		//Commit Details Date
		private Date dateOfCommit;
}
