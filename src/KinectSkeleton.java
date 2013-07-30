import SimpleOpenNI.SimpleOpenNI;
import processing.core.PVector;


public class KinectSkeleton {
	
	static SimpleOpenNI context;
	private int users = 0;
	
	public KinectSkeleton() {
	}
	
	public int getUsers() {
		return users;
	}

	public void setUsers(int users) {
		this.users = users;
	}

	public PVector[] update(int body, SimpleOpenNI context) {
		context.update();	
		
				
		// draw the skeleton if it's available
		int[] userList = context.getUsers();
		PVector[] jointPos_Proj = new PVector[userList.length];
		
		for (int i = 0; i < userList.length; i++) {
			if (context.isTrackingSkeleton(userList[i])) {
				users = userList.length;

				PVector pv = new PVector();
				// System.out.println(context.isTrackingSkeleton(userList[i]));
			
			context.getJointPositionSkeleton(userList[i],
					body, pv);
			System.out.println(pv);
			jointPos_Proj[i] = pv;
			}
		}
		
		// convert real world point to projective space
		return jointPos_Proj;
	}
		

}
