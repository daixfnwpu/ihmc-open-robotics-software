package us.ihmc.robotics.screwTheory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;

import us.ihmc.euclid.referenceFrame.FramePoint3D;
import us.ihmc.euclid.referenceFrame.FrameVector2D;
import us.ihmc.euclid.referenceFrame.FrameVector3D;
import us.ihmc.euclid.referenceFrame.ReferenceFrame;
import us.ihmc.euclid.tuple3D.Vector3D;

public class CenterOfMassJacobian
{
   private final ReferenceFrame rootFrame;
   private final List<RigidBody> rigidBodyList = new ArrayList<>();
   private final Set<RigidBody> rigidBodySet = new HashSet<>();
   private final InverseDynamicsJoint[] joints;

   private final DenseMatrix64F jacobianMatrix;
   private final Twist tempUnitTwist = new Twist();
   private final Vector3D tempJacobianColumn = new Vector3D();
   private final Vector3D tempVector = new Vector3D();
   private final FrameVector3D tempFrameVector = new FrameVector3D();
   private final DenseMatrix64F tempJointVelocitiesMatrix;
   private final DenseMatrix64F centerOfMassVelocityMatrix;

   private final Map<RigidBody, MutableDouble> subTreeMassMap = new LinkedHashMap<>();
   private final Map<RigidBody, FramePoint3D> comScaledByMassMap = new LinkedHashMap<>();
   private final Map<RigidBody, MutableBoolean> comScaledByMassMapIsUpdated = new LinkedHashMap<>();

   private double inverseTotalMass;

   public CenterOfMassJacobian(RigidBody rootBody)
   {
      this(ScrewTools.computeSupportAndSubtreeSuccessors(rootBody), rootBody.getBodyFixedFrame());
   }

   public CenterOfMassJacobian(RigidBody[] rigidBodies, ReferenceFrame rootFrame)
   {
      this(rigidBodies, ScrewTools.computeSupportJoints(rigidBodies), rootFrame);
   }

   public CenterOfMassJacobian(RigidBody[] rigidBodies, InverseDynamicsJoint[] joints, ReferenceFrame rootFrame)
   {
      this.rigidBodyList.addAll(Arrays.asList(rigidBodies));
      rigidBodySet.addAll(rigidBodyList);
      this.rootFrame = rootFrame;
      this.joints = joints;

      int size = ScrewTools.computeDegreesOfFreedom(this.joints);
      jacobianMatrix = new DenseMatrix64F(3, size);
      tempJointVelocitiesMatrix = new DenseMatrix64F(size, 1);

      centerOfMassVelocityMatrix = new DenseMatrix64F(jacobianMatrix.getNumRows(), 1);

      for (int i = 0; i < rigidBodyList.size(); i++)
      {
         RigidBody rigidBody = rigidBodyList.get(i);
         comScaledByMassMap.put(rigidBody, new FramePoint3D(rootFrame));
         comScaledByMassMapIsUpdated.put(rigidBody, new MutableBoolean(false));
         subTreeMassMap.put(rigidBody, new MutableDouble(-1.0));
      }

      recomputeSubtreeMassesAndTotalMass();
   }

   public void recomputeSubtreeMassesAndTotalMass()
   {
      for (int i = 0; i < rigidBodyList.size(); i++)
         subTreeMassMap.get(rigidBodyList.get(i)).setValue(-1.0);

      inverseTotalMass = 1.0 / TotalMassCalculator.computeMass(rigidBodyList);
   }

   public void compute()
   {
      int column = 0;
      for (int i = 0; i < rigidBodyList.size(); i++)
      {
         comScaledByMassMapIsUpdated.get(rigidBodyList.get(i)).setValue(false);
      }

      for (InverseDynamicsJoint joint : joints)
      {
         RigidBody childBody = joint.getSuccessor();

         FramePoint3D comPositionScaledByMass = getCoMScaledByMass(childBody);
         double subTreeMass = getSubTreeMass(childBody);

         for (int dofIndex = 0; dofIndex < joint.getDegreesOfFreedom(); dofIndex++)
         {
            joint.getUnitTwist(dofIndex, tempUnitTwist);
            tempUnitTwist.changeFrame(rootFrame);

            setColumn(tempUnitTwist, comPositionScaledByMass, subTreeMass, column);
            column++;
         }
      }

      CommonOps.scale(inverseTotalMass, jacobianMatrix);
   }

   private double getSubTreeMass(RigidBody rigidBody)
   {

      if (!subTreeMassMap.containsKey(rigidBody))
         subTreeMassMap.put(rigidBody, new MutableDouble(-1.0));

      MutableDouble subTreeMass = subTreeMassMap.get(rigidBody);

      if (subTreeMass.doubleValue() > 0.0)
      {
         return subTreeMass.doubleValue();
      }
      else
      {
         double curSubTreeMass = (rigidBodySet.contains(rigidBody) ? rigidBody.getInertia().getMass() : 0.0);
         List<InverseDynamicsJoint> childrenJoints = rigidBody.getChildrenJoints();
         for (int i = 0; i < childrenJoints.size(); i++)
         {
            double childSubTreeMass = getSubTreeMass(childrenJoints.get(i).getSuccessor());
            curSubTreeMass = curSubTreeMass + childSubTreeMass;
         }

         subTreeMass.setValue(curSubTreeMass);

         return curSubTreeMass;
      }
   }

   private FramePoint3D getCoMScaledByMass(RigidBody rigidBody)
   {
      MutableBoolean comIsUpdated = comScaledByMassMapIsUpdated.get(rigidBody);

      if (comIsUpdated == null)
      {
         comIsUpdated = new MutableBoolean(false);

         comScaledByMassMapIsUpdated.put(rigidBody, comIsUpdated);
         comScaledByMassMap.put(rigidBody, new FramePoint3D());
      }

      if (comIsUpdated.booleanValue())
      {
         return comScaledByMassMap.get(rigidBody);
      }
      else
      {
         FramePoint3D curChildCoMScaledByMass = comScaledByMassMap.get(rigidBody);

         curChildCoMScaledByMass.setToZero(rootFrame);
         rigidBody.getCoMOffset(curChildCoMScaledByMass);
         curChildCoMScaledByMass.changeFrame(rootFrame);
         double massToScale = (rigidBodySet.contains(rigidBody) ? rigidBody.getInertia().getMass() : 0.0);
         curChildCoMScaledByMass.scale(massToScale);

         final List<InverseDynamicsJoint> childrenJoints = rigidBody.getChildrenJoints();
         for (int i = 0; i < childrenJoints.size(); i++)
         {
            curChildCoMScaledByMass.add(getCoMScaledByMass(childrenJoints.get(i).getSuccessor()));
         }

         comScaledByMassMapIsUpdated.get(rigidBody).setValue(true);

         return curChildCoMScaledByMass;
      }
   }

   public DenseMatrix64F getMatrix()
   {
      return jacobianMatrix;
   }

   public void getCenterOfMassVelocity(FrameVector3D centerOfMassVelocityToPack)
   {
      ScrewTools.getJointVelocitiesMatrix(joints, tempJointVelocitiesMatrix);
      CommonOps.mult(jacobianMatrix, tempJointVelocitiesMatrix, centerOfMassVelocityMatrix);
      centerOfMassVelocityToPack.setIncludingFrame(rootFrame, centerOfMassVelocityMatrix);
   }

   public void getCenterOfMassVelocity(FrameVector2D centerOfMassVelocityToPack)
   {
      getCenterOfMassVelocity(rootFrame, centerOfMassVelocityToPack);
   }

   public void getCenterOfMassVelocity(ReferenceFrame desiredOutputFrame, FrameVector2D centerOfMassVelocityToPack)
   {
      getCenterOfMassVelocity(tempFrameVector);
      tempFrameVector.changeFrame(desiredOutputFrame);
      centerOfMassVelocityToPack.setIncludingFrame(tempFrameVector);
   }

   private void setColumn(Twist twist, FramePoint3D comPositionScaledByMass, double subTreeMass, int column)
   {
      tempVector.set(comPositionScaledByMass);
      tempVector.cross(twist.getAngularPart(), tempVector);
      tempJacobianColumn.set(tempVector);
      tempVector.set(twist.getLinearPart());
      tempVector.scale(subTreeMass);
      tempJacobianColumn.add(tempVector);

      jacobianMatrix.set(0, column, tempJacobianColumn.getX());
      jacobianMatrix.set(1, column, tempJacobianColumn.getY());
      jacobianMatrix.set(2, column, tempJacobianColumn.getZ());
   }
}
