package us.ihmc.footstepPlanning.scoring;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.vecmath.Point3d;

import org.junit.Test;

import us.ihmc.footstepPlanning.testTools.PlanningTestTools;
import us.ihmc.graphics3DDescription.appearance.AppearanceDefinition;
import us.ihmc.graphics3DDescription.appearance.YoAppearance;
import us.ihmc.graphics3DDescription.appearance.YoAppearanceRGBColor;
import us.ihmc.graphics3DDescription.yoGraphics.YoGraphicPolygon;
import us.ihmc.graphics3DDescription.yoGraphics.YoGraphicsListRegistry;
import us.ihmc.robotics.MathTools;
import us.ihmc.robotics.dataStructures.registry.YoVariableRegistry;
import us.ihmc.robotics.dataStructures.variable.DoubleYoVariable;
import us.ihmc.robotics.dataStructures.variable.IntegerYoVariable;
import us.ihmc.robotics.geometry.ConvexPolygon2d;
import us.ihmc.robotics.geometry.FramePose;
import us.ihmc.robotics.geometry.RigidBodyTransform;
import us.ihmc.robotics.math.frames.YoFramePose;
import us.ihmc.robotics.random.RandomTools;
import us.ihmc.robotics.referenceFrames.ReferenceFrame;
import us.ihmc.robotics.robotController.RobotControllerAdapter;
import us.ihmc.simulationconstructionset.Robot;
import us.ihmc.simulationconstructionset.SimulationConstructionSet;
import us.ihmc.simulationconstructionset.bambooTools.SimulationTestingParameters;
import us.ihmc.tools.color.Gradient;
import us.ihmc.tools.continuousIntegration.ContinuousIntegrationAnnotations.ContinuousIntegrationTest;
import us.ihmc.tools.continuousIntegration.ContinuousIntegrationTools;
import us.ihmc.tools.thread.ThreadTools;

public class PenalizationHeatmapStepScorerTest
{
   private Color[] scoreColorGradient;
   private Point3d footstepPlannerGoal = new Point3d(4.0, 1.0, 0.3);
   private ConvexPolygon2d defaultFootPolygon;
   private Map<Integer, YoGraphicPolygon> candidateFootstepPolygons;
   private DoubleYoVariable colorIndexYoVariable;
   private DoubleYoVariable stanceFootPitch;
   private DoubleYoVariable yoTime;
   private IntegerYoVariable candidateIndex;

   private double incrementX = 0.1;
   private double incrementY = 0.05;
   private double yMin = -1.0;
   private double xMax = 1.0;
   private double xMin = -0.8;
   private int numInX = (int) ((xMax - xMin) / incrementX);
   private int numInY = (int) ((xMax - yMin) / incrementY);

   @ContinuousIntegrationTest(estimatedDuration = 0.1)
   @Test(timeout = 3000000)
   public void testIdealFootstepAlwaysScoresBetterThanOthers()
   {
      Random random = new Random(1776L);

      YoVariableRegistry registry = new YoVariableRegistry("Test");
      YoGraphicsListRegistry yoGraphicsListRegistry = new YoGraphicsListRegistry();
      PenalizationHeatmapStepScorer footstepScorer = new PenalizationHeatmapStepScorer(registry, yoGraphicsListRegistry, null);

      ReferenceFrame worldFrame = ReferenceFrame.getWorldFrame();

      Point3d goal = new Point3d(100.0, 2.0, 3.0);

      int numberOfIdealStepsToTest = 1000;

      for (int i = 0; i < numberOfIdealStepsToTest; i++)
      {
         FramePose stanceFoot = new FramePose(worldFrame);
         stanceFoot.setPosition(RandomTools.generateRandomPoint(random, 1.0, 1.0, 0.3));
         stanceFoot.setOrientation(RandomTools.generateRandomQuaternion(random));

         FramePose swingStartFoot = new FramePose(worldFrame);
         swingStartFoot.setPosition(RandomTools.generateRandomPoint(random, 1.0, 1.0, 0.3));
         swingStartFoot.setOrientation(RandomTools.generateRandomQuaternion(random));

         FramePose idealFootstep = new FramePose(worldFrame);
         idealFootstep.setPosition(RandomTools.generateRandomPoint(random, 1.0, 1.0, 0.3));
         idealFootstep.setOrientation(RandomTools.generateRandomQuaternion(random));

         FramePose candidateFootstep = new FramePose(worldFrame);
         candidateFootstep.setPose(idealFootstep);

         double idealFootstepScore = footstepScorer.scoreFootstep(stanceFoot, swingStartFoot, idealFootstep, candidateFootstep, goal, 1.0);
         assertEquals(0.0, idealFootstepScore, 1e-7);
      }

      int numberOfRandomXYTranslations = 1000;

      for (int i = 0; i<numberOfRandomXYTranslations; i++)
      {
         FramePose stanceFoot = new FramePose(worldFrame);
         stanceFoot.setPosition(RandomTools.generateRandomPoint(random, 1.0, 1.0, 0.3));
         stanceFoot.setOrientation(RandomTools.generateRandomQuaternion(random));

         FramePose swingStartFoot = new FramePose(worldFrame);
         swingStartFoot.setPosition(RandomTools.generateRandomPoint(random, 1.0, 1.0, 0.3));
         swingStartFoot.setOrientation(RandomTools.generateRandomQuaternion(random));

         FramePose idealFootstep = new FramePose(worldFrame);
         idealFootstep.setPosition(RandomTools.generateRandomPoint(random, 1.0, 1.0, 0.3));
         idealFootstep.setOrientation(RandomTools.generateRandomQuaternion(random));

         FramePose candidateFootstep = new FramePose(worldFrame);         
         candidateFootstep.set(idealFootstep);

         RigidBodyTransform transform = new RigidBodyTransform();
         transform.setTranslation(RandomTools.generateRandomDouble(random, 0.1), RandomTools.generateRandomDouble(random, 0.1), 0.0);
         candidateFootstep.applyTransform(transform);
         double footstepScore = footstepScorer.scoreFootstep(stanceFoot, swingStartFoot, idealFootstep, candidateFootstep, goal, 1.0);

         assertTrue(footstepScore <= 0.0);
      }

   }

   @ContinuousIntegrationTest(estimatedDuration = 0.1)
   @Test(timeout = 3000000)
   public void testScoringFootsteps()
   {
      SimulationTestingParameters simulationTestingParameters = new SimulationTestingParameters();
      simulationTestingParameters.setCreateGUI(!ContinuousIntegrationTools.isRunningOnContinuousIntegrationServer());
      Robot robot = new Robot("Test");
      SimulationConstructionSet scs = new SimulationConstructionSet(robot, simulationTestingParameters);
      YoGraphicsListRegistry yoGraphicsListRegistry = new YoGraphicsListRegistry();
      YoVariableRegistry registry = scs.getRootRegistry();
      yoTime = (DoubleYoVariable) registry.getVariable("t");

      DoubleYoVariable scoreYoVariable = new DoubleYoVariable("score", registry);
      DoubleYoVariable xYoVariable = new DoubleYoVariable("x", registry);
      DoubleYoVariable yYoVariable = new DoubleYoVariable("y", registry);
      colorIndexYoVariable = new DoubleYoVariable("colorIndex", registry);
      stanceFootPitch = new DoubleYoVariable("stanceFootPitch", registry);
      candidateIndex = new IntegerYoVariable("candidateIndex", registry);

      PenalizationHeatmapStepScorer footstepScorer = new PenalizationHeatmapStepScorer(registry, yoGraphicsListRegistry, null);

      candidateFootstepPolygons = new HashMap<>();

      scoreColorGradient = Gradient.createGradient(Color.GREEN, Color.RED, 1000);
      stanceFootPitch.set(-0.2);

      defaultFootPolygon = PlanningTestTools.createDefaultFootPolygon();
      FramePose idealFramePose = new FramePose(ReferenceFrame.getWorldFrame());
      idealFramePose.setPosition(0.45, 0.0, 0.31);
      createStaticFootstep("ideal", idealFramePose, YoAppearance.HotPink(), registry, yoGraphicsListRegistry);
      FramePose swingStartFramePose = new FramePose(ReferenceFrame.getWorldFrame());
      swingStartFramePose.setPosition(0.0, 0.0, 0.31);
      createStaticFootstep("swingStart", swingStartFramePose, YoAppearance.Blue(), registry, yoGraphicsListRegistry);
      FramePose stanceFramePose = new FramePose(ReferenceFrame.getWorldFrame());
      stanceFramePose.setPosition(0.2, 0.26, 0.31);
      stanceFramePose.setYawPitchRoll(0.0, stanceFootPitch.getDoubleValue(), 0.0);
      YoGraphicPolygon stanceFootPolygon = createStaticFootstep("stance", stanceFramePose, YoAppearance.DarkGreen(), registry, yoGraphicsListRegistry);

      for (int candidateFootstepIndex = 0; candidateFootstepIndex < numInX * numInY; candidateFootstepIndex++)
      {
         createCandidateFootstep("candidate", candidateFootstepIndex, registry, yoGraphicsListRegistry);
      }

      robot.setController(new RobotControllerAdapter(new YoVariableRegistry("robotRegistry"))
      {
         @Override
         public void doControl()
         {
            stanceFootPolygon.setYawPitchRoll(0.0, stanceFootPitch.getDoubleValue(), 0.0);
            stanceFramePose.setYawPitchRoll(0.0, stanceFootPitch.getDoubleValue(), 0.0);

            FramePose candidateFramePose = new FramePose(ReferenceFrame.getWorldFrame());

            //applySlopeCandidateSet(candidateFramePose, xYoVariable, yYoVariable);
            applyVerticalCandidateSet(candidateFramePose, xYoVariable, yYoVariable);

            double score = footstepScorer.scoreFootstep(stanceFramePose, swingStartFramePose, idealFramePose, candidateFramePose, footstepPlannerGoal, 1.0);
            scoreYoVariable.set(score);

            if (candidateIndex.getIntegerValue() == 0)
            {
               for (YoGraphicPolygon candidateFootstepPolygon : candidateFootstepPolygons.values())
               {
                  candidateFootstepPolygon.setPoseToNaN();
               }
            }
            candidateFootstepPolygons.get(candidateIndex.getIntegerValue()).setPose(candidateFramePose);
            candidateFootstepPolygons.get(candidateIndex.getIntegerValue()).updateAppearance(getAppearanceForScore(scoreYoVariable.getDoubleValue()));
         }
      });

      scs.addYoGraphicsListRegistry(yoGraphicsListRegistry, true);
      scs.setCameraFix(0.0, 0.0, 0.0);
      scs.setCameraPosition(-1.0, 0.0, 30.0);
      scs.setScrollGraphsEnabled(false);
      scs.setMaxBufferSize(16000);
      scs.startOnAThread();

      scs.setDT(1, 1);
      scs.simulate(numInX * numInY);

      if (!ContinuousIntegrationTools.isRunningOnContinuousIntegrationServer())
      {
         ThreadTools.sleepForever();
      }
   }

   private void applySlopeCandidateSet(FramePose candidateFramePose, DoubleYoVariable xYoVariable, DoubleYoVariable yYoVariable)
   {
      candidateIndex.set((int) yoTime.getDoubleValue() % (numInX * numInY));

      double x = xMin + (((int) candidateIndex.getIntegerValue()) % numInX) * incrementX;
      double y = yMin + (((int) candidateIndex.getIntegerValue()) / numInX) * incrementY;
      xYoVariable.set(x);
      yYoVariable.set(y);

      candidateFramePose.setPosition(x, y, 0.3 + (y - 0.2) * 0.2);
   }

   private void applyVerticalCandidateSet(FramePose candidateFramePose, DoubleYoVariable xYoVariable, DoubleYoVariable yYoVariable)
   {
      double height = 1.0;
      double verticalIncrement = 0.005;

      candidateIndex.set((int) yoTime.getDoubleValue() % (int) (height / verticalIncrement));

      double x = 0.45;
      double y = 0.0;
      xYoVariable.set(x);
      yYoVariable.set(y);

      candidateFramePose.setPosition(x, y, candidateIndex.getIntegerValue() * verticalIncrement);
   }

   private void createCandidateFootstep(String name, int index, YoVariableRegistry registry, YoGraphicsListRegistry yoGraphicsListRegistry)
   {
      YoFramePose footstepYoFramePose = new YoFramePose(name + index + "FramePose", ReferenceFrame.getWorldFrame(), registry);
      footstepYoFramePose.setToNaN();
      YoGraphicPolygon footstepYoGraphicPolygon = new YoGraphicPolygon(name + index + "YoGraphicPolygon", footstepYoFramePose,
                                                                       defaultFootPolygon.getNumberOfVertices(), registry, 1.0, YoAppearance.Red());
      footstepYoGraphicPolygon.updateConvexPolygon2d(defaultFootPolygon);
      candidateFootstepPolygons.put(index, footstepYoGraphicPolygon);
      yoGraphicsListRegistry.registerYoGraphic(getClass().getSimpleName(), footstepYoGraphicPolygon);
   }

   private YoGraphicPolygon createStaticFootstep(String name, FramePose framePose, AppearanceDefinition appearance, YoVariableRegistry registry,
                                                 YoGraphicsListRegistry yoGraphicsListRegistry)
   {
      YoFramePose footstepYoFramePose = new YoFramePose(name + "FramePose", ReferenceFrame.getWorldFrame(), registry);
      footstepYoFramePose.set(framePose);
      YoGraphicPolygon footstepYoGraphicPolygon = new YoGraphicPolygon(name + "YoGraphicPolygon", footstepYoFramePose, defaultFootPolygon.getNumberOfVertices(),
                                                                       registry, 1.0, appearance);
      footstepYoGraphicPolygon.updateConvexPolygon2d(defaultFootPolygon);
      yoGraphicsListRegistry.registerYoGraphic(getClass().getSimpleName(), footstepYoGraphicPolygon);
      return footstepYoGraphicPolygon;
   }

   private AppearanceDefinition getAppearanceForScore(double score)
   {
      double greenScore = 0.0;
      double redScore = -1.5;

      score -= greenScore;
      score = -score;

      double index = score * 1000.0 / (greenScore - redScore);
      index = MathTools.clipToMinMax(index, 0, 999);

      colorIndexYoVariable.set(index);

      Color scoreColor = scoreColorGradient[(int) (index)];
      return new YoAppearanceRGBColor(scoreColor, 0.0);
   }
}
