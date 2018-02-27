package JGGameEngine.Support;

import java.util.ArrayList;

public class base64 {
	
	static char[] alphabet = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','0','1','2','3','4','5','6','7','8','9','+','/'};
    static char[] inalphabet = new char[256];
    static char[] decoder = new char[256];
	
	/**
	 * Decodes a 64base encoded memory. The decoded memory is
	 * expected to be freed by the caller.
	 *
	 * @returns the length of the out buffer
	 *
	 */
	//(unsigned char *in, unsigned int inLength, unsigned char **out)
	public static byte[] base64Decode(String in){

		byte[] outVal = null;
		
		_b64ReturnPackage ret = _base64Decode(in);
		int error = ret.error;
		ArrayList<Byte> bArr = ret.bytes;
		
		if( bArr != null ) {
			if (error > 0 ){
				System.out.println("Base64Utils: error decoding");
				outVal = null;
			}
			
			outVal = new byte[bArr.size()];
			for(int i = 0; i < bArr.size(); i++)
				outVal[i] = bArr.get(i).byteValue();
		}
	    return outVal;
	}
	// unsigned char *input, unsigned int input_len, unsigned char *output, unsigned int *output_len 
	private static _b64ReturnPackage _base64Decode(String input){
		
		_b64ReturnPackage outVal = new _b64ReturnPackage(); //create the output package

		int i, bits = 0, c = 0, char_count = 0;
		int input_idx = 0;

	    for (i = alphabet.length - 1; i >= 0 ; i--) {
			inalphabet[alphabet[i]] = (char)1;
			decoder[alphabet[i]] = (char)i;
	    }


		for( input_idx=0; input_idx < input.length() ; input_idx++ ) {
			c = input.charAt(input_idx);
			if (c == '=')
				break;
			if (c > 255 ||  inalphabet[c] == 0)
				continue;
			bits += decoder[c];
			char_count++;
			if (char_count == 4) {
				outVal.bytes.add(new Byte((byte)(bits >> 16)));
				outVal.bytes.add(new Byte((byte)((bits >> 8) & 0xff)));
				outVal.bytes.add(new Byte((byte)(bits & 0xff)));
				bits = 0;
				char_count = 0;
			} else {
				bits <<= 6;
			}
	    }

		if( c == '=' ) {
			switch (char_count) {
				case 1:
					System.out.println("base64Decode: encoding incomplete: at least 2 bits missing");
					outVal.error++;
					break;
				case 2:
					outVal.bytes.add(new Byte((byte)( bits >> 10 )));
					break;
				case 3:
					outVal.bytes.add(new Byte((byte)( bits >> 16 )));
					outVal.bytes.add(new Byte((byte)(( bits >> 8 ) & 0xff)));
					break;
				}
		} else if ( input_idx < input.length() ) {
			if (char_count != 0) {
				System.out.println("base64 encoding incomplete: at least "+ ((4 - char_count) * 6) + " bits truncated");
				outVal.error++;
			}
	    }

		return outVal;
	}

}

class _b64ReturnPackage {
	public int error;
	ArrayList<Byte> bytes;
	public _b64ReturnPackage(){
		error = 0;
		bytes = new ArrayList<Byte>();
	}
	public _b64ReturnPackage(int err, ArrayList<Byte> b){
		error = err;
		bytes = b;
	}
}
