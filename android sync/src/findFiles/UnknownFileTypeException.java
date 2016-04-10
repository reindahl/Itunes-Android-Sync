package findFiles;

public class UnknownFileTypeException extends Exception {

	
	private static final long serialVersionUID = -3751467706848011262L;

	public UnknownFileTypeException(){
		super();
	}
	
	public UnknownFileTypeException(String message){
		super(message);
		
	}
}
