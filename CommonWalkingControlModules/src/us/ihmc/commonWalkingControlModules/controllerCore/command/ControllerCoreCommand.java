package us.ihmc.commonWalkingControlModules.controllerCore.command;

import us.ihmc.commonWalkingControlModules.controllerCore.WholeBodyControllerCoreMode;
import us.ihmc.commonWalkingControlModules.controllerCore.command.feedbackController.FeedbackControlCommand;
import us.ihmc.commonWalkingControlModules.controllerCore.command.feedbackController.FeedbackControlCommandList;
import us.ihmc.commonWalkingControlModules.controllerCore.command.inverseDynamics.InverseDynamicsCommand;
import us.ihmc.commonWalkingControlModules.controllerCore.command.inverseDynamics.InverseDynamicsCommandList;
import us.ihmc.commonWalkingControlModules.controllerCore.command.inverseKinematics.InverseKinematicsCommand;
import us.ihmc.commonWalkingControlModules.controllerCore.command.inverseKinematics.InverseKinematicsCommandList;
import us.ihmc.commonWalkingControlModules.controllerCore.command.lowLevel.LowLevelOneDoFJointDesiredDataHolder;
import us.ihmc.commonWalkingControlModules.controllerCore.command.lowLevel.LowLevelOneDoFJointDesiredDataHolderReadOnly;

/**
 * Class that contains the different lists that are submitted the whole body controller core. All
 * desired behaviors to be considered must be submitted to the whole body controller core through
 * this command.
 */
public class ControllerCoreCommand implements ControllerCoreCommandInterface
{
   private final InverseDynamicsCommandList inverseDynamicsCommandList;
   private final InverseKinematicsCommandList inverseKinematicsCommandList;
   private final InverseDynamicsCommandList virtualModelControlCommandList;
   private final FeedbackControlCommandList feedbackControlCommandList;
   private final LowLevelOneDoFJointDesiredDataHolder lowLevelOneDoFJointDesiredDataHolder;
   private WholeBodyControllerCoreMode controllerCoreMode;

   /**
    * Class that contains the different lists that are submitted the whole body controller core. All
    * desired behaviors to be considered must be submitted to the whole body controller core through
    * this command.
    * 
    * @param controllerCoreMode desired controller core mode. Choose between Inverse Dynamics,
    *           Inverse Kinematics, or Virtual Model Control.
    */
   public ControllerCoreCommand(WholeBodyControllerCoreMode controllerCoreMode)
   {
      this.controllerCoreMode = controllerCoreMode;

      inverseDynamicsCommandList = new InverseDynamicsCommandList();
      virtualModelControlCommandList = new InverseDynamicsCommandList();
      feedbackControlCommandList = new FeedbackControlCommandList();
      inverseKinematicsCommandList = new InverseKinematicsCommandList();
      lowLevelOneDoFJointDesiredDataHolder = new LowLevelOneDoFJointDesiredDataHolder();
   }

   /**
    * Clears all the command lists.
    */
   public void clear()
   {
      inverseDynamicsCommandList.clear();
      feedbackControlCommandList.clear();
      inverseKinematicsCommandList.clear();
      lowLevelOneDoFJointDesiredDataHolder.clear();
   }

   /**
    * Adds command to be considered by the inverse dynamics controller core.
    * 
    * @param inverseDynamicsCommand command to add to the list
    */
   public void addInverseDynamicsCommand(InverseDynamicsCommand<?> inverseDynamicsCommand)
   {
      if (inverseDynamicsCommand != null)
         inverseDynamicsCommandList.addCommand(inverseDynamicsCommand);
   }

   /**
    * Adds command to be considered by the virtual model controller core.
    * 
    * @param inverseDynamicsCommand command to add to the list
    */
   public void addVirtualModelControlCommand(InverseDynamicsCommand<?> inverseDynamicsCommand)
   {
      if (inverseDynamicsCommand != null)
         virtualModelControlCommandList.addCommand(inverseDynamicsCommand);
   }

   /**
    * Adds a feedback controller command. Using this utilizes the controllers found in the inverse
    * dynamics and virtual model control modules. This command type is not considered by the inverse
    * kinematics controller core.
    * 
    * @param feedbackControlCommand feedback command to add to the list
    */
   public void addFeedbackControlCommand(FeedbackControlCommand<?> feedbackControlCommand)
   {
      if (feedbackControlCommand != null)
         feedbackControlCommandList.addCommand(feedbackControlCommand);
   }

   /**
    * Adds command to be considered by the inverse kinematics controller core.
    * 
    * @param inverseKinematicsCommand command to add to the list
    */
   public void addInverseKinematicsCommand(InverseKinematicsCommand<?> inverseKinematicsCommand)
   {
      if (inverseKinematicsCommand != null)
         inverseKinematicsCommandList.addCommand(inverseKinematicsCommand);
   }

   /**
    * Adds low level joint data to the existing low level joint data holder. These inputs overwrite
    * whatever comes out of the controller core. That is, if you want to guarantee a certain joint
    * output, bypassing the optimization process, submit that data here.
    * 
    * @param lowLevelJointData data to bypass the controller core with.
    */
   public void completeLowLevelJointData(LowLevelOneDoFJointDesiredDataHolderReadOnly lowLevelJointData)
   {
      if (lowLevelJointData != null)
         lowLevelOneDoFJointDesiredDataHolder.completeWith(lowLevelJointData);
   }

   /**
    * Used to set the desired controller core mode.
    * 
    * @param controllerCoreMode Desired controller core mode. Choose from Inverse Dynamics, Inverse
    *           Kinematics, and Virtual Model Control.
    */
   public void setControllerCoreMode(WholeBodyControllerCoreMode controllerCoreMode)
   {
      if (this.controllerCoreMode != controllerCoreMode)
      {
         clear();
         this.controllerCoreMode = controllerCoreMode;
      }
   }

   /**
    * @return the inverse dynamics command list to be considered by the inverse dynamics controller
    *         core.
    */
   @Override
   public InverseDynamicsCommandList getInverseDynamicsCommandList()
   {
      return inverseDynamicsCommandList;
   }

   /**
    * @return the inverse dynamics command list to be considered by the virtual model controller
    *         core.
    */
   @Override
   public InverseDynamicsCommandList getVirtualModelControlCommandList()
   {
      return virtualModelControlCommandList;
   }

   /**
    * @return the feedback control command list to be considered by the inverse dynamics controller
    *         core or the whole body controller core..
    */
   @Override
   public FeedbackControlCommandList getFeedbackControlCommandList()
   {
      return feedbackControlCommandList;
   }

   /**
    * @return the inverse dynamics command list to be considered by the inverse kinematics
    *         controller core.
    */
   @Override
   public InverseKinematicsCommandList getInverseKinematicsCommandList()
   {
      return inverseKinematicsCommandList;
   }

   /**
    * Contains the low level data that overwrites the whole body controller core output. Guarantees
    * certain joint data at the output.
    * 
    * @return low level joint desired data holder.
    */
   @Override
   public LowLevelOneDoFJointDesiredDataHolder getLowLevelOneDoFJointDesiredDataHolder()
   {
      return lowLevelOneDoFJointDesiredDataHolder;
   }

   /**
    * Set the controller core command data from an existing command.
    * 
    * @param other other controller core command to overwrite the current command.
    */
   public void set(ControllerCoreCommand other)
   {
      controllerCoreMode = other.controllerCoreMode;
      inverseDynamicsCommandList.set(other.inverseDynamicsCommandList);
      feedbackControlCommandList.set(other.feedbackControlCommandList);
      inverseKinematicsCommandList.set(other.inverseKinematicsCommandList);
      lowLevelOneDoFJointDesiredDataHolder.overwriteWith(lowLevelOneDoFJointDesiredDataHolder);
   }

   /**
    * @return desired controller core mode.
    */
   @Override
   public WholeBodyControllerCoreMode getControllerCoreMode()
   {
      return controllerCoreMode;
   }
}
