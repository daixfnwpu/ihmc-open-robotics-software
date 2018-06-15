package us.ihmc.commonWalkingControlModules.controlModules.flight;

import us.ihmc.commonWalkingControlModules.controllerCore.command.feedbackController.FeedbackControlCommand;
import us.ihmc.commonWalkingControlModules.controllerCore.command.inverseDynamics.InverseDynamicsCommand;

public interface ControlManagerInterface
{
   InverseDynamicsCommand<?> getInverseDynamicsCommand();
   FeedbackControlCommand<?> getFeedbackControlCommand();
   FeedbackControlCommand<?> createFeedbackControlTemplate();
   void initialize();
}
