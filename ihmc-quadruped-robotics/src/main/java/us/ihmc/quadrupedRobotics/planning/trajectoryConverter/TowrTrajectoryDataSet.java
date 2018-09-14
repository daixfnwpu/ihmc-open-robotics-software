package us.ihmc.quadrupedRobotics.planning.trajectoryConverter;

import org.ejml.data.DenseMatrix64F;
import us.ihmc.commons.lists.RecyclingArrayList;
import us.ihmc.euclid.tuple3D.Point3D;
import us.ihmc.euclid.tuple3D.Vector3D;
import us.ihmc.euclid.tuple3D.interfaces.Point3DReadOnly;
import us.ihmc.euclid.tuple3D.interfaces.Vector3DReadOnly;
import us.ihmc.euclid.tuple4D.Quaternion;
import us.ihmc.euclid.tuple4D.interfaces.QuaternionReadOnly;

public interface TowrTrajectoryDataSet
{
   int getNumberOfWayPoints();

   DenseMatrix64F getNumberOfSteps();

   DenseMatrix64F getTouchDownInstants();
   DenseMatrix64F getTakeOffInstants();
   
   DenseMatrix64F getFrontLeftFootPositionWorldFrame();
   DenseMatrix64F getFrontRightFootPositionWorldFrame();
   DenseMatrix64F getHindLeftFootPositionWorldFrame();
   DenseMatrix64F getHindRightFootPositionWorldFrame();

   RecyclingArrayList<Point3D> getCenterOfMassLinearPositions();
   RecyclingArrayList<Quaternion> getCenterOfMassAngularOrientations();

   RecyclingArrayList<Vector3D> getCenterOfMassLinearVelocities();
   RecyclingArrayList<Vector3D> getCenterOfMassAngularVelocities();

   RecyclingArrayList<Vector3D> getCenterOfMassLinearAccelerations();
   RecyclingArrayList<Vector3D> getCenterOfMassAngularAccelerations();

}