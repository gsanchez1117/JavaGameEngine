package JGGameEngine.ResourceCache;

import java.io.File;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

public class JGAudioPackageCache extends JGResourceCache {

	private static JGAudioPackageCache instance = null;
	
	public static JGAudioPackageCache sharedInstance(){
		if (instance == null){
			instance = new JGAudioPackageCache();
		}
		return instance;
	}
	
	public CachedAudioPackage getAudioPackage(String fileName){		
		return (CachedAudioPackage)super.getResource(fileName);
	}
	
	@Override
	public void loadResourceFromFile(String filename){
		//check if audio file is already in cache
		if (resources.containsKey(filename)){ return; }

		CachedAudioPackage pkg = null;
		try{
			
			File file = new File(filename);
			AudioInputStream ais = AudioSystem.getAudioInputStream(file);
			AudioFormat baseFormat = ais.getFormat();
			AudioFormat decodeFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
					baseFormat.getSampleRate(),
					16,
					baseFormat.getChannels(),
					baseFormat.getChannels() * 2,
					baseFormat.getSampleRate(),
					false);
			AudioInputStream dais = AudioSystem.getAudioInputStream(decodeFormat, ais);
			int size                          = (int) (decodeFormat.getFrameSize() * dais.getFrameLength());
	         byte[] audio                      = new byte[size];
	         DataLine.Info info                = new DataLine.Info(Clip.class, decodeFormat, size);
	         dais.read(audio, 0, size);

	          pkg = new CachedAudioPackage(decodeFormat, audio, info);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		if (pkg != null){
			resources.put(filename, pkg);
		}
	}
	
	public void removeAudioPackageFromCache(String fileName){
		super.removeResourceFromCache(fileName);
	}
	
	
	
	
	public static class CachedAudioPackage{
		
		public AudioFormat format;
		public byte[] data;
		public DataLine.Info info;
		
		public CachedAudioPackage(AudioFormat f, byte[] d, DataLine.Info i){
			format = f;
			data = d;
			info = i;
		}
	}
	
}

