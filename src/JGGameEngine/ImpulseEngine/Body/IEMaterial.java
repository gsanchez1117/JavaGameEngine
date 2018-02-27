package JGGameEngine.ImpulseEngine.Body;

public class IEMaterial {
	

	/**
	 * An enumeration of material types
	 * 	Rock       Density : 0.6  Restitution : 0.1
	 *	Wood       Density : 0.3  Restitution : 0.2
	 *	Metal      Density : 1.2  Restitution : 0.05
	 *	BouncyBall Density : 0.3  Restitution : 0.8
	 *	SuperBall  Density : 0.3  Restitution : 0.95
	 *	Pillow     Density : 0.1  Restitution : 0.2
	 */
	public enum IEMaterialType{ Rock, Wood, Metal, BouncyBall, SuperBall, Pillow }
	
	private float density;
	private float restitution;
	
	//////////////////
	//Constructors	//
	//////////////////
	
	/**
	 * Creates a new instance of IEMaterial with the given density and restitution values.
	 * @param Density - density of the material
	 * @param Restitution - restitution of the material
	 */
	public IEMaterial(float Density, float Restitution){
		density = Density;
		restitution = Restitution;
	}
	
	/**
	 * Creates a new instance of IEMaterial with a predefined type
	 * @param type - the type of material to use
	 */
	public IEMaterial(IEMaterialType type){
		switch(type){
			case Rock:
				density = 0.6f;
				restitution = 0.1f;
				break;
			case Wood:
				density = 0.3f;
				restitution = 0.2f;				
				break;
			case Metal:
				density = 1.2f;
				restitution = 0.05f;				
				break;
			case BouncyBall:
				density = 0.3f;
				restitution = 0.8f;				
				break;
			case SuperBall:
				density = 0.3f;
				restitution = 0.95f;				
				break;
			case Pillow:
				density = 0.1f;
				restitution = 0.2f;				
				break;
		}
	}
	
	//////////////////////
	//Setters / Getters	//
	//////////////////////
	
	/**
	 * Gets the density of the material
	 * @return - density as a float
	 */
	public float getDensity(){ return density; }
	
	/**
	 * Gets the restitution of the material
	 * @return - restitution as a float
	 */
	public float getRestitution(){ return restitution; }
}
