package JGGameEngine.ImpulseEngine.Body;

public class IEMassData {
	
	private float mass;
	private float invMass;
	private float inertia;
	private float invInertia;
	
	//////////////////
	//Constructors	//
	//////////////////
	
	/**
	 * Creates a new instance of IEMassData with the given mass and inertia.
	 * inverse mass and inverse inertia are automatically calculated.
	 * @param Mass - given mass
	 * @param Inertia - given inertia
	 */
	public IEMassData(float Mass, float Inertia){
		mass = Mass;
		invMass = (mass != 0) ? (1.0f / mass) : 0.0f;
		inertia = Inertia;
		invInertia = (inertia != 0) ? (1.0f / inertia) : 0.0f;
	}
	
	//////////////////////
	//Setters / Getters	//
	//////////////////////
	
	/**
	 * Gets the mass
	 * @return - mass as a float
	 */
	public float getMass(){ return mass; }
	
	/**
	 * Gets the inverse mass
	 * @return - inverse mass as a float
	 */
	public float getInvMass(){ return invMass; }	
	
	/**
	 * Gets the inertia
	 * @return - inertia as a float
	 */
	public float getInertia(){ return inertia; }
	
	/**
	 * Gets the inverse inertia
	 * @return - inverse inertia as a float
	 */
	public float getInvInertia(){ return invInertia; }

}
