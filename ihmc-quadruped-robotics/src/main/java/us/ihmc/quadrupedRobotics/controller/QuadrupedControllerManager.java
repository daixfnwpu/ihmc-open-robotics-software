package us.ihmc.quadrupedRobotics.controller;

import controller_msgs.msg.dds.QuadrupedControllerStateChangeMessage;
import controller_msgs.msg.dds.WalkingControllerFailureStatusMessage;
import us.ihmc.commonWalkingControlModules.configurations.HighLevelControllerParameters;
import us.ihmc.commonWalkingControlModules.controllerAPI.input.ControllerNetworkSubscriber;
import us.ihmc.commonWalkingControlModules.highLevelHumanoidControl.highLevelStates.*;
import us.ihmc.communication.ROS2Tools;
import us.ihmc.communication.controllerAPI.CommandInputManager;
import us.ihmc.communication.controllerAPI.StatusMessageOutputManager;
import us.ihmc.humanoidRobotics.communication.controllerAPI.converter.ClearDelayQueueConverter;
import us.ihmc.humanoidRobotics.communication.packets.dataobjects.HighLevelControllerName;
import us.ihmc.quadrupedRobotics.communication.QuadrupedControllerAPIDefinition;
import us.ihmc.quadrupedRobotics.communication.commands.QuadrupedRequestedControllerStateCommand;
import us.ihmc.quadrupedRobotics.controlModules.QuadrupedControlManagerFactory;
import us.ihmc.quadrupedRobotics.controller.states.QuadrupedWalkingControllerState;
import us.ihmc.quadrupedRobotics.model.QuadrupedPhysicalProperties;
import us.ihmc.quadrupedRobotics.model.QuadrupedRuntimeEnvironment;
import us.ihmc.quadrupedRobotics.output.JointIntegratorComponent;
import us.ihmc.quadrupedRobotics.output.OutputProcessorBuilder;
import us.ihmc.quadrupedRobotics.output.StateChangeSmootherComponent;
import us.ihmc.quadrupedRobotics.planning.ContactState;
import us.ihmc.robotics.robotController.OutputProcessor;
import us.ihmc.robotics.robotSide.RobotQuadrant;
import us.ihmc.robotics.screwTheory.OneDoFJoint;
import us.ihmc.robotics.stateMachine.core.State;
import us.ihmc.robotics.stateMachine.core.StateChangedListener;
import us.ihmc.robotics.stateMachine.core.StateMachine;
import us.ihmc.robotics.stateMachine.factories.StateMachineFactory;
import us.ihmc.ros2.RealtimeRos2Node;
import us.ihmc.sensorProcessing.model.RobotMotionStatusHolder;
import us.ihmc.sensorProcessing.outputData.JointDesiredOutputList;
import us.ihmc.simulationconstructionset.util.RobotController;
import us.ihmc.tools.thread.CloseableAndDisposable;
import us.ihmc.tools.thread.CloseableAndDisposableRegistry;
import us.ihmc.yoVariables.listener.VariableChangedListener;
import us.ihmc.yoVariables.registry.YoVariableRegistry;
import us.ihmc.yoVariables.variable.YoEnum;
import us.ihmc.yoVariables.variable.YoVariable;

import java.util.concurrent.atomic.AtomicReference;

/**
 * A {@link RobotController} for switching between other robot controllers according to an internal
 * finite state machine.
 * <p/>
 * Users can manually fire events on the {@code userTrigger} YoVariable.
 */
public class QuadrupedControllerManager implements RobotController, CloseableAndDisposable
{
   private final CloseableAndDisposableRegistry closeableAndDisposableRegistry = new CloseableAndDisposableRegistry();

   private final YoVariableRegistry registry = new YoVariableRegistry(getClass().getSimpleName());
   private final YoEnum<QuadrupedControllerRequestedEvent> requestedControllerState = new YoEnum<>("requestedControllerState", registry,
                                                                                                   QuadrupedControllerRequestedEvent.class, true);
   private final YoEnum<QuadrupedControllerRequestedEvent> lastEvent = new YoEnum<>("lastEvent", registry, QuadrupedControllerRequestedEvent.class);

   private final RobotMotionStatusHolder motionStatusHolder = new RobotMotionStatusHolder();

   private final StateMachine<QuadrupedControllerEnum, State> stateMachine;
   private final QuadrupedRuntimeEnvironment runtimeEnvironment;
   private final QuadrupedControllerToolbox controllerToolbox;
   private final QuadrupedControlManagerFactory controlManagerFactory;
   private final OutputProcessor outputProcessor;

   private final CommandInputManager commandInputManager;
   private final StatusMessageOutputManager statusMessageOutputManager;

   private final QuadrupedControllerStateChangeMessage quadrupedControllerStateChangeMessage = new QuadrupedControllerStateChangeMessage();
   private final WalkingControllerFailureStatusMessage walkingControllerFailureStatusMessage = new WalkingControllerFailureStatusMessage();

   private final AtomicReference<QuadrupedControllerRequestedEvent> requestedEvent = new AtomicReference<>();

   public QuadrupedControllerManager(QuadrupedRuntimeEnvironment runtimeEnvironment, QuadrupedPhysicalProperties physicalProperties)
   {
      this.controllerToolbox = new QuadrupedControllerToolbox(runtimeEnvironment, physicalProperties, registry, runtimeEnvironment.getGraphicsListRegistry());
      this.runtimeEnvironment = runtimeEnvironment;

      // Initialize control modules
      this.controlManagerFactory = new QuadrupedControlManagerFactory(controllerToolbox, physicalProperties, runtimeEnvironment.getGraphicsListRegistry(),
                                                                      registry);

      commandInputManager = new CommandInputManager(QuadrupedControllerAPIDefinition.getQuadrupedSupportedCommands());
      try
      {
         commandInputManager.registerConversionHelper(new ClearDelayQueueConverter(QuadrupedControllerAPIDefinition.getQuadrupedSupportedCommands()));
      }
      catch (InstantiationException | IllegalAccessException e)
      {
         e.printStackTrace();
      }
      statusMessageOutputManager = new StatusMessageOutputManager(QuadrupedControllerAPIDefinition.getQuadrupedSupportedStatusMessages());

      controlManagerFactory.getOrCreateFeetManager();
      controlManagerFactory.getOrCreateBodyOrientationManager();
      controlManagerFactory.getOrCreateBalanceManager();
      controlManagerFactory.getOrCreateJointSpaceManager();

      // Initialize output processor
      StateChangeSmootherComponent stateChangeSmootherComponent = new StateChangeSmootherComponent(runtimeEnvironment, registry);
      JointIntegratorComponent jointControlComponent = new JointIntegratorComponent(runtimeEnvironment, registry);
      controlManagerFactory.getOrCreateFeetManager().attachStateChangedListener(stateChangeSmootherComponent.createFiniteStateMachineStateChangedListener());
      OutputProcessorBuilder outputProcessorBuilder = new OutputProcessorBuilder(runtimeEnvironment.getFullRobotModel());
      outputProcessorBuilder.addComponent(stateChangeSmootherComponent);
      outputProcessorBuilder.addComponent(jointControlComponent);
      outputProcessor = outputProcessorBuilder.build();

      requestedControllerState.set(null);
      requestedControllerState.addVariableChangedListener(new VariableChangedListener()
      {
         @Override
         public void notifyOfVariableChange(YoVariable<?> v)
         {
            QuadrupedControllerRequestedEvent requestedControllerEvent = requestedControllerState.getEnumValue();
            if (requestedControllerEvent != null)
            {
               requestedEvent.set(requestedControllerEvent);
               requestedControllerState.set(null);
            }
         }
      });

      this.stateMachine = buildStateMachine(runtimeEnvironment);
   }

   public State getState(QuadrupedControllerEnum state)
   {
      return stateMachine.getState(state);
   }

   @Override
   public void initialize()
   {
      outputProcessor.initialize();
   }

   @Override
   public void doControl()
   {
      if (commandInputManager.isNewCommandAvailable(QuadrupedRequestedControllerStateCommand.class))
      {
         requestedEvent.set(commandInputManager.pollNewestCommand(QuadrupedRequestedControllerStateCommand.class).getRequestedControllerState());
      }

      // update requested events
      QuadrupedControllerRequestedEvent reqEvent = requestedEvent.getAndSet(null);
      if (reqEvent != null)
      {
         lastEvent.set(reqEvent);
         requestedControllerState.set(reqEvent);
      }

      // update controller state machine
      stateMachine.doActionAndTransition();

      // update contact state used for state estimation
      switch (stateMachine.getCurrentStateKey())
      {
      case DO_NOTHING:
      case STAND_PREP:
      case STAND_READY:
         for (RobotQuadrant robotQuadrant : RobotQuadrant.values)
         {
            runtimeEnvironment.getFootSwitches().get(robotQuadrant).trustFootSwitch(false);
         }

         runtimeEnvironment.getFootSwitches().get(RobotQuadrant.FRONT_LEFT).setFootContactState(false);
         runtimeEnvironment.getFootSwitches().get(RobotQuadrant.FRONT_RIGHT).setFootContactState(false);
         runtimeEnvironment.getFootSwitches().get(RobotQuadrant.HIND_LEFT).setFootContactState(false);
         runtimeEnvironment.getFootSwitches().get(RobotQuadrant.HIND_RIGHT).setFootContactState(true);
         break;
      default:
         for (RobotQuadrant robotQuadrant : RobotQuadrant.values)
         {
            runtimeEnvironment.getFootSwitches().get(robotQuadrant).trustFootSwitch(true);
            if (controllerToolbox.getContactState(robotQuadrant) == ContactState.IN_CONTACT)
            {
               runtimeEnvironment.getFootSwitches().get(robotQuadrant).setFootContactState(true);
            }
            else
            {
               runtimeEnvironment.getFootSwitches().get(robotQuadrant).setFootContactState(false);
            }
         }
         break;
      }

      // update fall detector
      if (controllerToolbox.getFallDetector().detect())
      {
         requestedControllerState.set(QuadrupedControllerRequestedEvent.REQUEST_FALL);
         walkingControllerFailureStatusMessage.falling_direction_.set(runtimeEnvironment.getFullRobotModel().getRootJoint().getLinearVelocityForReading());
         statusMessageOutputManager.reportStatusMessage(walkingControllerFailureStatusMessage);
      }

      // update output processor
      outputProcessor.update();
   }

   @Override
   public YoVariableRegistry getYoVariableRegistry()
   {
      return registry;
   }

   @Override
   public String getName()
   {
      return getClass().getSimpleName();
   }

   @Override
   public String getDescription()
   {
      return "A proxy controller for switching between multiple subcontrollers";
   }

   public RobotMotionStatusHolder getMotionStatusHolder()
   {
      return motionStatusHolder;
   }

   private StateMachine<QuadrupedControllerEnum, State> buildStateMachine(QuadrupedRuntimeEnvironment runtimeEnvironment)
   {
      OneDoFJoint[] controlledJoints = runtimeEnvironment.getFullRobotModel().getControllableOneDoFJoints();
      HighLevelControllerParameters highLevelControllerParameters = runtimeEnvironment.getHighLevelControllerParameters();
      JointDesiredOutputList jointDesiredOutputList = runtimeEnvironment.getJointDesiredOutputList();

      DoNothingControllerState doNothingState = new DoNothingControllerState(controlledJoints, highLevelControllerParameters);
      StandPrepControllerState standPrepState = new StandPrepControllerState(controlledJoints, highLevelControllerParameters, jointDesiredOutputList);
      StandReadyControllerState standReadyState = new StandReadyControllerState(controlledJoints, highLevelControllerParameters, jointDesiredOutputList);
      QuadrupedWalkingControllerState walkingState = new QuadrupedWalkingControllerState(runtimeEnvironment, controllerToolbox, commandInputManager,
                                                                                         statusMessageOutputManager, controlManagerFactory, registry);
      SmoothTransitionControllerState standTransitionState = new SmoothTransitionControllerState("toWalking", HighLevelControllerName.STAND_TRANSITION_STATE,
                                                                                                 standReadyState, walkingState, controlledJoints,
                                                                                                 highLevelControllerParameters);
      SmoothTransitionControllerState exitWalkingState = new SmoothTransitionControllerState("exitWalking", HighLevelControllerName.EXIT_WALKING, walkingState,
                                                                                             standPrepState, controlledJoints, highLevelControllerParameters);
      FreezeControllerState freezeState = new FreezeControllerState(controlledJoints, highLevelControllerParameters, jointDesiredOutputList);

      StateMachineFactory<QuadrupedControllerEnum, State> factory = new StateMachineFactory<>(QuadrupedControllerEnum.class);
      factory.setNamePrefix("controller").setRegistry(registry).buildYoClock(runtimeEnvironment.getRobotTimestamp());

      factory.addState(QuadrupedControllerEnum.DO_NOTHING, doNothingState);
      factory.addState(QuadrupedControllerEnum.STAND_PREP, standPrepState);
      factory.addState(QuadrupedControllerEnum.STAND_READY, standReadyState);
      factory.addState(QuadrupedControllerEnum.FREEZE, freezeState);
      factory.addState(QuadrupedControllerEnum.STEPPING, walkingState);

      // Add automatic transitions that lead into the stand state.
      factory.addDoneTransition(QuadrupedControllerEnum.STAND_PREP, QuadrupedControllerEnum.STAND_READY);

      // Manually triggered events to transition to main controllers.
      factory.addTransition(QuadrupedControllerEnum.STAND_READY, QuadrupedControllerEnum.STEPPING, time -> requestedControllerState.getEnumValue() == QuadrupedControllerRequestedEvent.REQUEST_STEPPING);
      factory.addTransition(QuadrupedControllerEnum.DO_NOTHING, QuadrupedControllerEnum.STEPPING, time -> requestedControllerState.getEnumValue() == QuadrupedControllerRequestedEvent.REQUEST_STEPPING);
      factory.addTransition(QuadrupedControllerEnum.FREEZE, QuadrupedControllerEnum.STEPPING, time -> requestedControllerState.getEnumValue() == QuadrupedControllerRequestedEvent.REQUEST_STEPPING);
      factory.addTransition(QuadrupedControllerEnum.STAND_READY, QuadrupedControllerEnum.STAND_PREP, time -> requestedControllerState.getEnumValue() == QuadrupedControllerRequestedEvent.REQUEST_STAND_PREP);
      factory.addTransition(QuadrupedControllerEnum.FREEZE, QuadrupedControllerEnum.STAND_PREP, time -> requestedControllerState.getEnumValue() == QuadrupedControllerRequestedEvent.REQUEST_STAND_PREP);
      factory.addTransition(QuadrupedControllerEnum.DO_NOTHING, QuadrupedControllerEnum.FREEZE, time -> requestedControllerState.getEnumValue() == QuadrupedControllerRequestedEvent.REQUEST_FREEZE);
      factory.addTransition(QuadrupedControllerEnum.STEPPING, QuadrupedControllerEnum.FREEZE, time -> requestedControllerState.getEnumValue() == QuadrupedControllerRequestedEvent.REQUEST_FREEZE);
      factory.addTransition(QuadrupedControllerEnum.STAND_PREP, QuadrupedControllerEnum.FREEZE, time -> requestedControllerState.getEnumValue() == QuadrupedControllerRequestedEvent.REQUEST_FREEZE);
      factory.addTransition(QuadrupedControllerEnum.STAND_READY, QuadrupedControllerEnum.FREEZE, time -> requestedControllerState.getEnumValue() == QuadrupedControllerRequestedEvent.REQUEST_FREEZE);

      // Trigger do nothing
      for (QuadrupedControllerEnum state : QuadrupedControllerEnum.values)
      {
         factory.addTransition(state, QuadrupedControllerEnum.DO_NOTHING, time -> requestedControllerState.getEnumValue() == QuadrupedControllerRequestedEvent.REQUEST_DO_NOTHING);
      }

      // Fall triggered events
      factory.addTransition(QuadrupedControllerEnum.STEPPING, QuadrupedControllerEnum.FREEZE, time -> requestedControllerState.getEnumValue() == QuadrupedControllerRequestedEvent.REQUEST_FALL);

      // Transitions from controllers back to stand prep.
      factory.addTransition(QuadrupedControllerEnum.DO_NOTHING, QuadrupedControllerEnum.STAND_PREP, time -> requestedControllerState.getEnumValue() == QuadrupedControllerRequestedEvent.REQUEST_STAND_PREP);
      factory.addTransition(QuadrupedControllerEnum.STEPPING, QuadrupedControllerEnum.STAND_PREP, time -> requestedControllerState.getEnumValue() == QuadrupedControllerRequestedEvent.REQUEST_STAND_PREP);

      factory.addStateChangedListener(new StateChangedListener<QuadrupedControllerEnum>()
      {
         @Override
         public void stateChanged(QuadrupedControllerEnum from, QuadrupedControllerEnum to)
         {
            byte fromByte = from == null ? -1 : from.toByte();
            byte toByte = to == null ? -1 : to.toByte();
            quadrupedControllerStateChangeMessage.setInitialQuadrupedControllerEnum(fromByte);
            quadrupedControllerStateChangeMessage.setEndQuadrupedControllerEnum(toByte);
            statusMessageOutputManager.reportStatusMessage(quadrupedControllerStateChangeMessage);
         }
      });

      return factory.build(QuadrupedControllerEnum.DO_NOTHING);
   }

   public void createControllerNetworkSubscriber(String robotName, RealtimeRos2Node realtimeRos2Node)
   {
      ROS2Tools.MessageTopicNameGenerator subscriberTopicNameGenerator = QuadrupedControllerAPIDefinition.getSubscriberTopicNameGenerator(robotName);
      ROS2Tools.MessageTopicNameGenerator publisherTopicNameGenerator = QuadrupedControllerAPIDefinition.getPublisherTopicNameGenerator(robotName);
      ControllerNetworkSubscriber controllerNetworkSubscriber = new ControllerNetworkSubscriber(subscriberTopicNameGenerator, commandInputManager,
                                                                                                publisherTopicNameGenerator, statusMessageOutputManager,
                                                                                                realtimeRos2Node);
      controllerNetworkSubscriber.addMessageCollector(QuadrupedControllerAPIDefinition.createDefaultMessageIDExtractor());
      controllerNetworkSubscriber.addMessageValidator(QuadrupedControllerAPIDefinition.createDefaultMessageValidation());
   }

   public void resetSteppingState()
   {
      QuadrupedWalkingControllerState steppingState = (QuadrupedWalkingControllerState) stateMachine.getState(QuadrupedControllerEnum.STEPPING);
      steppingState.onEntry();
   }

   @Override
   public void closeAndDispose()
   {
      closeableAndDisposableRegistry.closeAndDispose();
   }
}
