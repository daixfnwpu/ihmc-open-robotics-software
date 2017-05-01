package us.ihmc.commonWalkingControlModules.trajectories;

import us.ihmc.robotics.MathTools;
import us.ihmc.robotics.dataStructures.registry.YoVariableRegistry;
import us.ihmc.robotics.dataStructures.variable.DoubleYoVariable;
import us.ihmc.robotics.geometry.FrameOrientation;
import us.ihmc.robotics.geometry.FramePoint;
import us.ihmc.robotics.geometry.FramePose;
import us.ihmc.robotics.geometry.FrameVector;
import us.ihmc.robotics.math.trajectories.PoseTrajectoryGenerator;
import us.ihmc.robotics.math.trajectories.YoSpline3D;
import us.ihmc.robotics.referenceFrames.ReferenceFrame;

public class SoftTouchdownPoseTrajectoryGenerator implements PoseTrajectoryGenerator
{
   private static final ReferenceFrame worldFrame = ReferenceFrame.getWorldFrame();
   private final YoVariableRegistry registry;
   
   private final YoSpline3D positionTouchdownTrajectory;
   private final FramePoint desiredPosition = new FramePoint();
   
   private final DoubleYoVariable timeInitial;
   private final DoubleYoVariable timeFinal;
   private final DoubleYoVariable timeIntoTouchdown;
   
   private final FramePoint initialPosition = new FramePoint();
   private final FrameVector initialVelocity = new FrameVector();
   private final FrameVector initialAcceleration = new FrameVector();
   
   private final FrameOrientation constantOrientation = new FrameOrientation();
   private final FrameVector constantAngularVelocity = new FrameVector();
   private final FrameVector constantAngularAcceleration = new FrameVector();
   
   public SoftTouchdownPoseTrajectoryGenerator(String namePrefix, YoVariableRegistry parentRegistry)
   {
      registry = new YoVariableRegistry(namePrefix + getClass().getSimpleName());
      parentRegistry.addChild(registry);
      
      positionTouchdownTrajectory = new YoSpline3D(3, 3, worldFrame, registry, namePrefix + "Trajectory");
      timeInitial = new DoubleYoVariable(namePrefix + "TimeInitial", registry);
      timeFinal = new DoubleYoVariable(namePrefix + "TimeFinal", registry);
      timeIntoTouchdown = new DoubleYoVariable(namePrefix + "TimeIntoTouchdown", registry);
      
      timeFinal.set(Double.POSITIVE_INFINITY);
   }
   
   public void setOrientation(FrameOrientation orientation)
   {
      constantOrientation.setIncludingFrame(orientation);
      constantAngularVelocity.setToZero(worldFrame);
      constantAngularAcceleration.setToZero(worldFrame);
   }

   public void setLinearTrajectory(double initialTime, FramePoint initialPosition, FrameVector initialVelocity, FrameVector initialAcceleration)
   {
      this.timeInitial.set(initialTime);
      
      this.initialPosition.setIncludingFrame(initialPosition);
      this.initialVelocity.setIncludingFrame(initialVelocity);
      this.initialAcceleration.setIncludingFrame(initialAcceleration);
      
      initialPosition.changeFrame(worldFrame);
      initialVelocity.changeFrame(worldFrame);
      initialAcceleration.changeFrame(worldFrame);
   }

   @Override
   public void initialize()
   {
      double t0 = timeInitial.getDoubleValue();
      double tf = timeFinal.getDoubleValue();
      positionTouchdownTrajectory.setQuadraticUsingInitialVelocityAndAcceleration(t0, tf, initialPosition, initialVelocity, initialAcceleration);
   }

   @Override
   public void compute(double time)
   {
      double clippedTime = MathTools.clamp(time, timeInitial.getDoubleValue(), timeFinal.getDoubleValue());
      timeIntoTouchdown.set(clippedTime - timeInitial.getDoubleValue());
      positionTouchdownTrajectory.compute(clippedTime);
   }

   @Override
   public boolean isDone()
   {
      return false;
   }

   @Override
   public void getPosition(FramePoint positionToPack)
   {
      positionTouchdownTrajectory.getPosition(positionToPack);
   }

   @Override
   public void getVelocity(FrameVector velocityToPack)
   {
      positionTouchdownTrajectory.getVelocity(velocityToPack);
   }

   @Override
   public void getAcceleration(FrameVector accelerationToPack)
   {
      positionTouchdownTrajectory.getAcceleration(accelerationToPack);
   }

   @Override
   public void getOrientation(FrameOrientation orientationToPack)
   {
      orientationToPack.setIncludingFrame(constantOrientation);
   }

   @Override
   public void getAngularVelocity(FrameVector angularVelocityToPack)
   {
      angularVelocityToPack.setIncludingFrame(constantAngularVelocity);
   }

   @Override
   public void getAngularAcceleration(FrameVector angularAccelerationToPack)
   {
      angularAccelerationToPack.setIncludingFrame(constantAngularAcceleration);
   }

   @Override
   public void getPose(FramePose framePoseToPack)
   {
      getPosition(desiredPosition);
      framePoseToPack.setPose(desiredPosition, constantOrientation);
   }
   
   @Override
   public void showVisualization()
   {
   }

   @Override
   public void hideVisualization()
   {
   }
}