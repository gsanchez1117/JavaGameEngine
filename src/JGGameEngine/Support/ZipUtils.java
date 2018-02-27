package JGGameEngine.Support;

import java.io.ByteArrayOutputStream;
import java.io.IOException; 
import java.util.zip.Deflater;  
import java.util.zip.Inflater; 

public class ZipUtils {
	
	  //private static final Logger LOG = Logger.getLogger(CompressionUtils.class);  
	
	
	public static byte[] compress(byte[] data) throws IOException { 
		Deflater deflater = new Deflater();  
		deflater.setInput(data);  
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);   
		deflater.finish();  
	   	byte[] buffer = new byte[1024];   
	   	while (!deflater.finished()) {  
	   		int count = deflater.deflate(buffer); // returns the generated code... index  
	    	outputStream.write(buffer, 0, count);   
	   	}  
	   	outputStream.close();  
	   	byte[] output = outputStream.toByteArray();  
	   	//LOG.debug("Original: " + data.length / 1024 + " Kb");  
	   	//LOG.debug("Compressed: " + output.length / 1024 + " Kb");  
	   	return output;  
	}  
	  
	  
	public static byte[] decompress(byte[] data){// throws IOException, DataFormatException {  
		Inflater inflater = new Inflater();   
		inflater.setInput(data);  
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);  
		byte[] output = null;
		try{
			byte[] buffer = new byte[1024];
			while (!inflater.finished()) {  
				int count = inflater.inflate(buffer);  
				outputStream.write(buffer, 0, count);  
			}  
			outputStream.close(); 
			output = outputStream.toByteArray();
		}catch(Exception e){
			System.out.println(e.getMessage());
		}   
		//LOG.debug("Original: " + data.length);  
		//LOG.debug("Compressed: " + output.length);  
		return output;  
	} 
	
	public static byte[] decompressDiv4(byte[] data){
		byte[] b4 = decompress(data);
		byte[] b = new byte[b4.length/4];
		for (int i = 0; i < b.length; i++)
			b[i] = b4[i*4];
		return b;
	}
}
