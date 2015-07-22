package findFiles;

public class FileTypeUnknown extends Exception {

	
	private static final long serialVersionUID = -3751467706848011262L;

	public FileTypeUnknown(){
		super();
	}
	
	public FileTypeUnknown(String message){
		super(message);
		
	}
}
