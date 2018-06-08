package JGGameEngine.Sound;

import java.io.File;
import java.util.HashMap;

import JGGameEngine.JGPoint;
import JGGameEngine.JGUtilities;
import JGGameEngine.ExternalLibs.tinysound.Music;
import JGGameEngine.ExternalLibs.tinysound.Sound;
import JGGameEngine.ExternalLibs.tinysound.TinySound;

public class JGSimpleAudioEngine {
	
	static JGSimpleAudioEngine instance = null;
	HashMap<String, Music> music;
	HashMap<String, Sound> sounds;
	
	public static JGSimpleAudioEngine SharedInstance(){
		
		if (instance == null){
			instance = new JGSimpleAudioEngine();
		}
		return instance;
	}
	
	private JGSimpleAudioEngine(){
		TinySound.init();
		music = new HashMap<String, Music>();
		sounds = new HashMap<String, Sound>();
	}
	
	public Sound loadSoundEffect(String fileName){
		if (!sounds.containsKey(fileName)){
			File file = new File(fileName);
			Sound s = TinySound.loadSound(file);
			sounds.put(fileName, s);
			return s;
		}else{
			return sounds.get(fileName);
		}
	}
	
	public Music loadMusic(String fileName){
		if (!music.containsKey(fileName)){
			File file = new File(fileName);
			Music m = TinySound.loadMusic(file);
			music.put(fileName, m);
			return m;
		}else{
			return music.get(fileName);
		}
	}
	
	public void playSoundEffect(String fileName, double volume, double panning){
		if (sounds.containsKey(fileName)){
			Sound s = sounds.get(fileName);
			s.play(volume, panning);
		}else{
			Sound s = loadSoundEffect(fileName);
			s.play(volume, panning);
		}
	}
	
	public void playSoundEffect(String fileName){
		playSoundEffect(fileName, 1.0f, 0.0f);
	}
	
	public void playSoundEffect(String fileName, JGPoint position, JGPoint listenerPos){
			
		double dx = listenerPos.x-position.x;
	    double dy = listenerPos.y-position.y;
	    double distance = dx*dx+dy*dy;
	    double volume = 0;
	    double panning;

	    if (distance <= 250000){
	    	volume = (1.0f-distance*(1.0f/250000.0f));
	        volume = JGUtilities.clamp(volume, 0.0f, 1.0f);
	        if (distance > 25) {
	            panning = -(dx/distance);
	            panning = JGUtilities.clamp(panning, -1.0f, 1.0f);
	        }
	        else {
	            panning = 0;
	        }      
	        playSoundEffect(fileName, volume, panning);
	    }
	}
	
	public void stopSoundEffect(String fileName){
		if (sounds.containsKey(fileName)){
			Sound s = sounds.get(fileName);
			s.stop();
		}
	}
	
	public void playMusic(String fileName, boolean loops){
		if (music.containsKey(fileName)){
			Music m = music.get(fileName);
			m.play(loops);
		}else{
			Music m = loadMusic(fileName);
			m.play(loops);
		}
	}
	
	public void pauseMusic(String fileName){
		if (music.containsKey(fileName)){
			Music m = music.get(fileName);
			m.pause();
		}
	}
	
	public void resumeMusic(String fileName){
		if (music.containsKey(fileName)){
			Music m = music.get(fileName);
			m.resume();
		}
	}
	
	public void stopMusic(String fileName){
		if (music.containsKey(fileName)){
			Music m = music.get(fileName);
			m.stop();;
		}
	}
	
	public void shutDown(){
		TinySound.shutdown();
	}

}
