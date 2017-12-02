package us.ihmc.avatar.networkProcessor.kinematicsToolboxModule;

import static org.junit.Assert.assertTrue;
import static us.ihmc.humanoidRobotics.communication.packets.KinematicsToolboxMessageFactory.holdRigidBodyCurrentPose;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import us.ihmc.avatar.collisionAvoidance.RobotCollisionMeshProvider;
import us.ihmc.avatar.jointAnglesWriter.JointAnglesWriter;
import us.ihmc.commons.PrintTools;
import us.ihmc.commons.RandomNumbers;
import us.ihmc.commons.thread.ThreadTools;
import us.ihmc.communication.controllerAPI.CommandInputManager;
import us.ihmc.communication.controllerAPI.StatusMessageOutputManager;
import us.ihmc.communication.packets.KinematicsToolboxRigidBodyMessage;
import us.ihmc.euclid.referenceFrame.FramePoint3D;
import us.ihmc.euclid.referenceFrame.ReferenceFrame;
import us.ihmc.euclid.tuple3D.Point3D;
import us.ihmc.geometry.polytope.ConvexPolytopeConstructor;
import us.ihmc.geometry.polytope.DCELPolytope.Frame.FrameConvexPolytope;
import us.ihmc.graphicsDescription.appearance.YoAppearanceRGBColor;
import us.ihmc.graphicsDescription.instructions.Graphics3DInstruction;
import us.ihmc.graphicsDescription.instructions.Graphics3DPrimitiveInstruction;
import us.ihmc.graphicsDescription.yoGraphics.YoGraphicsListRegistry;
import us.ihmc.robotics.robotController.RobotController;
import us.ihmc.robotics.robotDescription.JointDescription;
import us.ihmc.robotics.robotDescription.LinkDescription;
import us.ihmc.robotics.robotDescription.LinkGraphicsDescription;
import us.ihmc.robotics.robotDescription.RobotDescription;
import us.ihmc.robotics.screwTheory.FloatingInverseDynamicsJoint;
import us.ihmc.robotics.screwTheory.OneDoFJoint;
import us.ihmc.robotics.screwTheory.RigidBody;
import us.ihmc.robotics.screwTheory.ScrewTools;
import us.ihmc.robotics.sensors.ForceSensorDefinition;
import us.ihmc.robotics.sensors.IMUDefinition;
import us.ihmc.sensorProcessing.communication.packets.dataobjects.RobotConfigurationData;
import us.ihmc.simulationconstructionset.Robot;
import us.ihmc.simulationconstructionset.RobotFromDescription;
import us.ihmc.simulationconstructionset.SimulationConstructionSet;
import us.ihmc.simulationconstructionset.util.simulationRunner.BlockingSimulationRunner;
import us.ihmc.simulationconstructionset.util.simulationRunner.BlockingSimulationRunner.SimulationExceededMaximumTimeException;
import us.ihmc.simulationconstructionset.util.simulationTesting.SimulationTestingParameters;
import us.ihmc.yoVariables.registry.YoVariableRegistry;
import us.ihmc.yoVariables.variable.YoBoolean;
import us.ihmc.yoVariables.variable.YoDouble;
import us.ihmc.yoVariables.variable.YoInteger;

public class KinematicsToolboxCollisionDetectionTest
{
   private static final boolean VERBOSE = true;

   private static final ReferenceFrame worldFrame = ReferenceFrame.getWorldFrame();
   private static final YoAppearanceRGBColor ghostApperance = new YoAppearanceRGBColor(Color.YELLOW, 0.75);
   private static final SimulationTestingParameters simulationTestingParameters = SimulationTestingParameters.createFromSystemProperties();
   private static final boolean visualize = simulationTestingParameters.getCreateGUI();
   static
   {
      simulationTestingParameters.setKeepSCSUp(true);
      simulationTestingParameters.setDataBufferSize(1 << 16);
   }

   private CommandInputManager commandInputManager;
   private YoVariableRegistry mainRegistry;
   private YoGraphicsListRegistry yoGraphicsListRegistry;
   private KinematicsToolboxController toolboxController;

   private YoBoolean initializationSucceeded;
   private YoInteger numberOfIterations;
   private YoDouble finalSolutionQuality;

   private SimulationConstructionSet scs;
   private BlockingSimulationRunner blockingSimulationRunner;

   private Robot robot;
   private Robot ghost;
   private RobotController toolboxUpdater;

   @Before
   public void setup()
   {
      mainRegistry = new YoVariableRegistry("main");
      initializationSucceeded = new YoBoolean("initializationSucceeded", mainRegistry);
      numberOfIterations = new YoInteger("numberOfIterations", mainRegistry);
      finalSolutionQuality = new YoDouble("finalSolutionQuality", mainRegistry);
      yoGraphicsListRegistry = new YoGraphicsListRegistry();

      RobotDescription robotDescription = new KinematicsToolboxControllerTestRobots.SevenDoFArm();
      Pair<FloatingInverseDynamicsJoint, OneDoFJoint[]> desiredFullRobotModel = KinematicsToolboxControllerTestRobots.createInverseDynamicsRobot(robotDescription);
      commandInputManager = new CommandInputManager(KinematicsToolboxModule.supportedCommands());
      RigidBody rootBody = ScrewTools.getRootBody(desiredFullRobotModel.getRight()[0].getSuccessor());
      commandInputManager.registerConversionHelper(new KinematicsToolboxCommandConverter(rootBody));

      StatusMessageOutputManager statusOutputManager = new StatusMessageOutputManager(KinematicsToolboxModule.supportedStatus());

      toolboxController = new KinematicsToolboxController(commandInputManager, statusOutputManager, desiredFullRobotModel.getLeft(),
                                                          desiredFullRobotModel.getRight(), yoGraphicsListRegistry, mainRegistry);

      RobotCollisionMeshProvider robotCollisionMeshProvider = new RobotCollisionMeshProvider(8);
      Map<RigidBody, FrameConvexPolytope> collisionMeshes = robotCollisionMeshProvider.createCollisionMeshesFromRobotDescription(rootBody,
                                                                                                                                 robotDescription.getRootJoints()
                                                                                                                                                 .get(0));
      toolboxController.setCollisionMeshes(collisionMeshes);

      robot = new RobotFromDescription(robotDescription);
      toolboxUpdater = createToolboxUpdater();
      robot.setController(toolboxUpdater);
      robot.setDynamic(false);
      robot.setGravity(0);

      RobotDescription ghostRobotDescription = new KinematicsToolboxControllerTestRobots.SevenDoFArm();
      ghostRobotDescription.setName("Ghost");
      recursivelyModyfyGraphics(ghostRobotDescription.getChildrenJoints().get(0));
      ghost = new RobotFromDescription(ghostRobotDescription);
      ghost.setDynamic(false);
      ghost.setGravity(0);

      if (visualize)
      {
         scs = new SimulationConstructionSet(new Robot[] {robot, ghost}, simulationTestingParameters);
         scs.addYoGraphicsListRegistry(yoGraphicsListRegistry, true);
         scs.setCameraFix(0.0, 0.0, 1.0);
         scs.setCameraPosition(8.0, 0.0, 3.0);
         scs.startOnAThread();
         scs.setDT(0.001, 1);
         blockingSimulationRunner = new BlockingSimulationRunner(scs, 60.0 * 10.0);
      }
   }

   private void snapGhostToFullRobotModel(Pair<FloatingInverseDynamicsJoint, OneDoFJoint[]> fullHumanoidRobotModel)
   {
      new JointAnglesWriter(ghost, fullHumanoidRobotModel.getLeft(), fullHumanoidRobotModel.getRight()).updateRobotConfigurationBasedOnFullRobotModel();
   }

   @After
   public void tearDown()
   {
      if (simulationTestingParameters.getKeepSCSUp())
         ThreadTools.sleepForever();

      if (mainRegistry != null)
      {
         mainRegistry.closeAndDispose();
         mainRegistry = null;
      }

      initializationSucceeded = null;

      yoGraphicsListRegistry = null;

      commandInputManager = null;

      toolboxController = null;

      robot = null;
      toolboxUpdater = null;
      blockingSimulationRunner = null;

      if (scs != null)
      {
         scs.closeAndDispose();
         scs = null;
      }
   }

   @Test
   public void testHoldBodyPose() throws Exception
   {
      Pair<FloatingInverseDynamicsJoint, OneDoFJoint[]> initialFullRobotModel = createFullRobotModelAtInitialConfiguration();
      snapGhostToFullRobotModel(initialFullRobotModel);

      RigidBody hand = ScrewTools.findRigidBodiesWithNames(ScrewTools.computeRigidBodiesAfterThisJoint(initialFullRobotModel.getRight()), "handLink")[0];
      commandInputManager.submitMessage(holdRigidBodyCurrentPose(hand));

      RobotConfigurationData robotConfigurationData = extractRobotConfigurationData(initialFullRobotModel);
      toolboxController.updateRobotConfigurationData(robotConfigurationData);

      int numberOfIterations = 2;

      runKinematicsToolboxController(numberOfIterations);

      assertTrue(KinematicsToolboxController.class.getSimpleName() + " did not manage to initialize.", initializationSucceeded.getBooleanValue());
      assertTrue("Poor solution quality: " + toolboxController.getSolution().getSolutionQuality(),
                 toolboxController.getSolution().getSolutionQuality() < 1.0e-4);
   }

   @Test
   public void testHandCollision() throws Exception
   {
      if (VERBOSE)
         PrintTools.info(this, "Entering: testRandomHandPositions");
      FrameConvexPolytope obstacle = ConvexPolytopeConstructor.getFrameCuboidCollisionMesh(ReferenceFrame.getWorldFrame(), new Point3D(0.15, 0.0, 0.65), 0.15,
                                                                                           0.15, 0.15);
      toolboxController.submitObstacleCollisionMesh(obstacle);
      Pair<FloatingInverseDynamicsJoint, OneDoFJoint[]> initialFullRobotModel = createFullRobotModelAtInitialConfiguration();
      RobotConfigurationData robotConfigurationData = extractRobotConfigurationData(initialFullRobotModel);

      Pair<FloatingInverseDynamicsJoint, OneDoFJoint[]> desiredFullRobotModel = createFullRobotModelAtInitialConfiguration();
      OneDoFJoint[] inputAngles = desiredFullRobotModel.getRight();
      inputAngles[0].setQ(Math.toRadians(0));
      inputAngles[1].setQ(Math.toRadians(0));
      inputAngles[2].setQ(Math.toRadians(90));
      inputAngles[3].setQ(Math.toRadians(-90));
      inputAngles[4].setQ(Math.toRadians(45));
      inputAngles[5].setQ(Math.toRadians(45));
      inputAngles[6].setQ(Math.toRadians(45));

      toolboxController.enableCollisionAvoidance(true);
      RigidBody hand = ScrewTools.findRigidBodiesWithNames(ScrewTools.computeRigidBodiesAfterThisJoint(desiredFullRobotModel.getRight()), "handLink")[0];
      FramePoint3D desiredPosition = new FramePoint3D(hand.getBodyFixedFrame());
      desiredPosition.changeFrame(worldFrame);
      KinematicsToolboxRigidBodyMessage message = new KinematicsToolboxRigidBodyMessage(hand, desiredPosition);
      message.setWeight(20.0);
      commandInputManager.submitMessage(message);

      snapGhostToFullRobotModel(desiredFullRobotModel);
      toolboxController.updateRobotConfigurationData(robotConfigurationData);

      int numberOfIterations = 2000;

      runKinematicsToolboxController(numberOfIterations);

      assertTrue(KinematicsToolboxController.class.getSimpleName() + " did not manage to initialize.", initializationSucceeded.getBooleanValue());
      double solutionQuality = toolboxController.getSolution().getSolutionQuality();
      if (VERBOSE)
         PrintTools.info(this, "Solution quality: " + solutionQuality);
      assertTrue("Poor solution quality: " + solutionQuality, solutionQuality < 1.0e-4);
   }

   @Test
   public void testRandomHandPosition() throws SimulationExceededMaximumTimeException
   {
      for (int i = 0; i < 10; i++)
      {
         Pair<FloatingInverseDynamicsJoint, OneDoFJoint[]> initialFullRobotModel = createFullRobotModelAtInitialConfiguration();

         Pair<FloatingInverseDynamicsJoint, OneDoFJoint[]> desiredFullRobotModel = createFullRobotModelAtInitialConfiguration();
         randomizeJointPositions(new Random(), desiredFullRobotModel, 0.6);

         toolboxController.enableCollisionAvoidance(false);
         RigidBody desiredHand = ScrewTools.findRigidBodiesWithNames(ScrewTools.computeRigidBodiesAfterThisJoint(desiredFullRobotModel.getRight()),
                                                                     "handLink")[0];
         FramePoint3D desiredPosition = new FramePoint3D(desiredHand.getBodyFixedFrame());
         desiredPosition.changeFrame(worldFrame);
         KinematicsToolboxRigidBodyMessage message = new KinematicsToolboxRigidBodyMessage(desiredHand, desiredPosition);
         message.setWeight(20.0);
         commandInputManager.submitMessage(message);

         snapGhostToFullRobotModel(desiredFullRobotModel);
         randomizeJointPositions(new Random(), initialFullRobotModel, 0.6);
         RobotConfigurationData robotConfigurationData = extractRobotConfigurationData(initialFullRobotModel);
         toolboxController.updateRobotConfigurationData(robotConfigurationData);

         int numberOfIterations = 200;
         runKinematicsToolboxController(numberOfIterations);

         RigidBody lowerArm = ScrewTools.findRigidBodiesWithNames(ScrewTools.computeRigidBodiesAfterThisJoint(toolboxController.getDesiredOneDoFJoint()),
                                                                  "lowerArmLink")[0];
         //RigidBody hand = ScrewTools.findRigidBodiesWithNames(ScrewTools.computeRigidBodiesAfterThisJoint(controllerFullRobotModel.getOneDoFJoints()),
         //                                                     "handLink")[0];
         FramePoint3D pointForObstacle = new FramePoint3D(lowerArm.getBodyFixedFrame());
         pointForObstacle.add(RandomNumbers.nextDouble(new Random(), -0.01, 0.01), RandomNumbers.nextDouble(new Random(), -0.01, 0.01),
                              RandomNumbers.nextDouble(new Random(), -0.01, 0.01));
         //pointForObstacle.changeFrame(hand.getBodyFixedFrame());
         //pointForObstacle.scale(0.5);
         pointForObstacle.changeFrame(worldFrame);
         FrameConvexPolytope obstacleMesh = ConvexPolytopeConstructor.getFrameSphericalCollisionMeshByProjectingCube(pointForObstacle, 0.075, 4);
         toolboxController.clearObstacleMeshes();
         toolboxController.submitObstacleCollisionMesh(obstacleMesh);

         toolboxController.enableCollisionAvoidance(true);
         toolboxController.updateRobotConfigurationData(robotConfigurationData);
         commandInputManager.submitMessage(message);
         runKinematicsToolboxController(numberOfIterations * 2);

         System.gc();
         assertTrue(KinematicsToolboxController.class.getSimpleName() + " did not manage to initialize.", initializationSucceeded.getBooleanValue());
         double solutionQuality = toolboxController.getSolution().getSolutionQuality();
         if (VERBOSE)
            PrintTools.info(this, "Solution quality: " + solutionQuality);
         assertTrue("Poor solution quality: " + solutionQuality, solutionQuality < 1.0e-4);
      }
   }

   private void randomizeJointPositions(Random random, Pair<FloatingInverseDynamicsJoint, OneDoFJoint[]> randomizedFullRobotModel,
                                        double percentOfMotionRangeAllowed)
   {
      for (OneDoFJoint joint : randomizedFullRobotModel.getRight())
      {
         double jointLimitLower = joint.getJointLimitLower();
         if (Double.isInfinite(jointLimitLower))
            jointLimitLower = -Math.PI;
         double jointLimitUpper = joint.getJointLimitUpper();
         if (Double.isInfinite(jointLimitUpper))
            jointLimitUpper = -Math.PI;
         double rangeReduction = (1.0 - percentOfMotionRangeAllowed) * (jointLimitUpper - jointLimitLower);
         jointLimitLower += 0.5 * rangeReduction;
         jointLimitUpper -= 0.5 * rangeReduction;
         joint.setQ(RandomNumbers.nextDouble(random, jointLimitLower, jointLimitUpper));
      }
   }

   private Pair<FloatingInverseDynamicsJoint, OneDoFJoint[]> createFullRobotModelAtInitialConfiguration()
   {
      RobotDescription robotDescription = new KinematicsToolboxControllerTestRobots.SevenDoFArm();
      Pair<FloatingInverseDynamicsJoint, OneDoFJoint[]> fullRobotModel = KinematicsToolboxControllerTestRobots.createInverseDynamicsRobot(robotDescription);
      for (OneDoFJoint joint : fullRobotModel.getRight())
      {
         if (Double.isFinite(joint.getJointLimitLower()) && Double.isFinite(joint.getJointLimitUpper()))
            joint.setQ(0.5 * (joint.getJointLimitLower() + joint.getJointLimitUpper()));
      }
      return fullRobotModel;
   }

   private void runKinematicsToolboxController(int numberOfIterations) throws SimulationExceededMaximumTimeException
   {
      initializationSucceeded.set(false);
      this.numberOfIterations.set(0);

      if (visualize)
      {
         blockingSimulationRunner.simulateNTicksAndBlockAndCatchExceptions(numberOfIterations);
      }
      else
      {
         for (int i = 0; i < numberOfIterations; i++)
            toolboxUpdater.doControl();
      }

      finalSolutionQuality.set(toolboxController.getSolution().getSolutionQuality());
   }

   private RobotController createToolboxUpdater()
   {
      return new RobotController()
      {
         private final JointAnglesWriter jointAnglesWriter = new JointAnglesWriter(robot, toolboxController.getDesiredRootJoint(),
                                                                                   toolboxController.getDesiredOneDoFJoint());

         @Override
         public void doControl()
         {
            if (!initializationSucceeded.getBooleanValue())
               initializationSucceeded.set(toolboxController.initialize());

            if (initializationSucceeded.getBooleanValue())
            {
               toolboxController.updateInternal();
               jointAnglesWriter.updateRobotConfigurationBasedOnFullRobotModel();
               numberOfIterations.increment();
            }
         }

         @Override
         public void initialize()
         {
         }

         @Override
         public YoVariableRegistry getYoVariableRegistry()
         {
            return mainRegistry;
         }

         @Override
         public String getName()
         {
            return mainRegistry.getName();
         }

         @Override
         public String getDescription()
         {
            return null;
         }
      };
   }

   private static void recursivelyModyfyGraphics(JointDescription joint)
   {
      if (joint == null)
         return;
      LinkDescription link = joint.getLink();
      if (link == null)
         return;
      LinkGraphicsDescription linkGraphics = link.getLinkGraphics();

      if (linkGraphics != null)
      {
         ArrayList<Graphics3DPrimitiveInstruction> graphics3dInstructions = linkGraphics.getGraphics3DInstructions();

         if (graphics3dInstructions == null)
            return;

         for (Graphics3DPrimitiveInstruction primitive : graphics3dInstructions)
         {
            if (primitive instanceof Graphics3DInstruction)
            {
               Graphics3DInstruction modelInstruction = (Graphics3DInstruction) primitive;
               modelInstruction.setAppearance(ghostApperance);
            }
         }
      }

      if (joint.getChildrenJoints() == null)
         return;

      for (JointDescription child : joint.getChildrenJoints())
      {
         recursivelyModyfyGraphics(child);
      }
   }

   private RobotConfigurationData extractRobotConfigurationData(Pair<FloatingInverseDynamicsJoint, OneDoFJoint[]> initialFullRobotModel)
   {
      OneDoFJoint[] joints = initialFullRobotModel.getRight();
      RobotConfigurationData robotConfigurationData = new RobotConfigurationData(joints, new ForceSensorDefinition[0], null, new IMUDefinition[0]);
      robotConfigurationData.setJointState(Arrays.stream(joints).collect(Collectors.toList()));

      FloatingInverseDynamicsJoint rootJoint = initialFullRobotModel.getLeft();
      if (rootJoint != null)
      {
         robotConfigurationData.setRootTranslation(rootJoint.getTranslationForReading());
         robotConfigurationData.setRootOrientation(rootJoint.getRotationForReading());
      }
      return robotConfigurationData;
   }
}
