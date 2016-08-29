package us.ihmc.robotics.generatedTestSuites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

import us.ihmc.tools.testing.TestPlanSuite;
import us.ihmc.tools.testing.TestPlanSuite.TestSuiteTarget;
import us.ihmc.tools.testing.TestPlanTarget;

/** WARNING: AUTO-GENERATED FILE. DO NOT MAKE MANUAL CHANGES TO THIS FILE. **/
@RunWith(TestPlanSuite.class)
@TestSuiteTarget(TestPlanTarget.Fast)
@SuiteClasses
({
   us.ihmc.robotics.alphaToAlpha.StretchedSlowInMiddleAlphaToAlphaFunctionTest.class,
   us.ihmc.robotics.controllers.CylindricalPDGainsTest.class,
   us.ihmc.robotics.controllers.GainCalculatorTest.class,
   us.ihmc.robotics.controllers.PDControllerTest.class,
   us.ihmc.robotics.controllers.PIDControllerTest.class,
   us.ihmc.robotics.controllers.YoPDGainsTest.class,
   us.ihmc.robotics.controllers.YoPIDGainsTest.class,
   us.ihmc.robotics.dataStructures.ComplexNumberTest.class,
   us.ihmc.robotics.dataStructures.DoubleHashHeightMapTest.class,
   us.ihmc.robotics.dataStructures.PolynomialTest.class,
   us.ihmc.robotics.dataStructures.registry.NameSpaceTest.class,
   us.ihmc.robotics.dataStructures.registry.YoVariableRegistryTest.class,
   us.ihmc.robotics.dataStructures.variable.BooleanYoVariableTest.class,
   us.ihmc.robotics.dataStructures.variable.DoubleYoVariableTest.class,
   us.ihmc.robotics.dataStructures.variable.EnumYoVariableTest.class,
   us.ihmc.robotics.dataStructures.variable.IntegerYoVariableTest.class,
   us.ihmc.robotics.dataStructures.variable.LongYoVariableTest.class,
   us.ihmc.robotics.dataStructures.variable.YoVariableListTest.class,
   us.ihmc.robotics.dataStructures.variable.YoVariableTest.class,
   us.ihmc.robotics.filters.GlitchFilterForDataSetTest.class,
   us.ihmc.robotics.filters.ZeroLagLowPassFilterTest.class,
   us.ihmc.robotics.functionApproximation.LinearMappingTest.class,
   us.ihmc.robotics.functionApproximation.LinearRegressionTest.class,
   us.ihmc.robotics.functionApproximation.OnlineLinearRegressionTest.class,
   us.ihmc.robotics.geometry.AngleToolsTest.class,
   us.ihmc.robotics.geometry.BoundingBox2dTest.class,
   us.ihmc.robotics.geometry.BoundingBox3dTest.class,
   us.ihmc.robotics.geometry.CapsuleTest.class,
   us.ihmc.robotics.geometry.ConvexHullCalculator2dTest.class,
   us.ihmc.robotics.geometry.ConvexPolygon2dTest.class,
   us.ihmc.robotics.geometry.ConvexPolygonShrinkerTest.class,
   us.ihmc.robotics.geometry.ConvexPolygonToolsTest.class,
   us.ihmc.robotics.geometry.DirectionTest.class,
   us.ihmc.robotics.geometry.FrameConvexPolygon2dTest.class,
   us.ihmc.robotics.geometry.FrameLine2dTest.class,
   us.ihmc.robotics.geometry.FrameLineTest.class,
   us.ihmc.robotics.geometry.FrameMatrix3DTest.class,
   us.ihmc.robotics.geometry.FrameOrientationTest.class,
   us.ihmc.robotics.geometry.FramePoint2dTest.class,
   us.ihmc.robotics.geometry.FramePointTest.class,
   us.ihmc.robotics.geometry.FramePoseTest.class,
   us.ihmc.robotics.geometry.FrameVector2dTest.class,
   us.ihmc.robotics.geometry.FrameVectorTest.class,
   us.ihmc.robotics.geometry.GeometryToolsTest.class,
   us.ihmc.robotics.geometry.GeoregressionConversionToolsTest.class,
   us.ihmc.robotics.geometry.HumanEvaluationLine2dTest.class,
   us.ihmc.robotics.geometry.InertiaToolsTest.class,
   us.ihmc.robotics.geometry.InPlaceConvexHullCalculator2dTest.class,
   us.ihmc.robotics.geometry.LeastSquaresZPlaneFitterTest.class,
   us.ihmc.robotics.geometry.Line2dTest.class,
   us.ihmc.robotics.geometry.LineSegment2dTest.class,
   us.ihmc.robotics.geometry.LineSegment3dTest.class,
   us.ihmc.robotics.geometry.PointToLineUnProjectorTest.class,
   us.ihmc.robotics.geometry.QuaternionRotationRelationshipTest.class,
   us.ihmc.robotics.geometry.RigidBodyTransformTest.class,
   us.ihmc.robotics.geometry.RotationalInertiaCalculatorTest.class,
   us.ihmc.robotics.geometry.RotationToolsTest.class,
   us.ihmc.robotics.geometry.shapes.BestFitPlaneCalculatorTest.class,
   us.ihmc.robotics.geometry.shapes.Box3dTest.class,
   us.ihmc.robotics.geometry.shapes.Cylinder3dTest.class,
   us.ihmc.robotics.geometry.shapes.Ellipsoid3dTest.class,
   us.ihmc.robotics.geometry.shapes.FrameBox3dTest.class,
   us.ihmc.robotics.geometry.shapes.FramePlane3dTest.class,
   us.ihmc.robotics.geometry.shapes.Plane3dTest.class,
   us.ihmc.robotics.geometry.shapes.QuickHull3DWrapperTest.class,
   us.ihmc.robotics.geometry.shapes.Ramp3dTest.class,
   us.ihmc.robotics.geometry.shapes.Sphere3dTest.class,
   us.ihmc.robotics.geometry.shapes.Torus3dTest.class,
   us.ihmc.robotics.geometry.SmallAngleRotationDerivativeTest.class,
   us.ihmc.robotics.geometry.SmallQuaternionRelationshipTest.class,
   us.ihmc.robotics.geometry.StringStretcher2dTest.class,
   us.ihmc.robotics.geometry.Transform3dTest.class,
   us.ihmc.robotics.geometry.TransformToolsTest.class,
   us.ihmc.robotics.hyperCubeTree.HyperCubeNodeTest.class,
   us.ihmc.robotics.hyperCubeTree.HyperCubeTreeTest.class,
   us.ihmc.robotics.hyperCubeTree.OctreeTest.class,
   us.ihmc.robotics.hyperCubeTree.OneDimensionalBoundsTest.class,
   us.ihmc.robotics.hyperCubeTree.SphericalLinearResolutionProviderTest.class,
   us.ihmc.robotics.kinematics.AverageQuaternionCalculatorTest.class,
   us.ihmc.robotics.kinematics.DdoglegInverseKinematicsCalculatorTest.class,
   us.ihmc.robotics.kinematics.fourbar.FourBarCalculatorsComparisonTest.class,
   us.ihmc.robotics.kinematics.fourbar.FourBarCalculatorTest.class,
   us.ihmc.robotics.kinematics.fourbar.FourBarCalculatorWithDerivativesTest.class,
   us.ihmc.robotics.kinematics.NumericalInverseKinematicsCalculatorTest.class,
   us.ihmc.robotics.kinematics.TimeStampedTransform3DTest.class,
   us.ihmc.robotics.kinematics.TransformInterpolationCalculatorTest.class,
   us.ihmc.robotics.lidar.AbstractLidarScanTest.class,
   us.ihmc.robotics.lidar.LidarScanParametersTest.class,
   us.ihmc.robotics.lidar.LidarScanTest.class,
   us.ihmc.robotics.linearAlgebra.MatrixExponentialCalculatorTest.class,
   us.ihmc.robotics.linearAlgebra.MatrixOfCofactorsCalculatorInefficientTest.class,
   us.ihmc.robotics.linearAlgebra.MatrixStatisticsTest.class,
   us.ihmc.robotics.linearAlgebra.MatrixToolsTest.class,
   us.ihmc.robotics.linearAlgebra.NullspaceCalculatorTest.class,
   us.ihmc.robotics.linearAlgebra.PrincipalComponentAnalysis3DTest.class,
   us.ihmc.robotics.linearDynamicSystems.BodeUnitsConverterTest.class,
   us.ihmc.robotics.linearDynamicSystems.ComplexConjugateModeTest.class,
   us.ihmc.robotics.linearDynamicSystems.ComplexMatrixTest.class,
   us.ihmc.robotics.linearDynamicSystems.DoubleMassSpringOscillatorTest.class,
   us.ihmc.robotics.linearDynamicSystems.EigenvalueDecomposerTest.class,
   us.ihmc.robotics.linearDynamicSystems.LinearDynamicSystemTest.class,
   us.ihmc.robotics.linearDynamicSystems.MassSpringDamperTest.class,
   us.ihmc.robotics.linearDynamicSystems.PolynomialMatrixTest.class,
   us.ihmc.robotics.linearDynamicSystems.SingleRealModeTest.class,
   us.ihmc.robotics.linearDynamicSystems.StateSpaceSystemDiscretizerTest.class,
   us.ihmc.robotics.linearDynamicSystems.TransferFunctionMatrixTest.class,
   us.ihmc.robotics.linearDynamicSystems.TransferFunctionTest.class,
   us.ihmc.robotics.lists.RecyclingArrayListTest.class,
   us.ihmc.robotics.math.corruptors.NoisyDoubleYoVariableTest.class,
   us.ihmc.robotics.math.corruptors.NoisyYoRotationMatrixTest.class,
   us.ihmc.robotics.math.filters.AlphaBetaFilteredYoVariableTest.class,
   us.ihmc.robotics.math.filters.AlphaFilteredWrappingYoVariableTest.class,
   us.ihmc.robotics.math.filters.AlphaFilteredYoFrameQuaternionTest.class,
   us.ihmc.robotics.math.filters.AlphaFilteredYoVariableTest.class,
   us.ihmc.robotics.math.filters.AlphaFusedYoVariableTest.class,
   us.ihmc.robotics.math.filters.BacklashCompensatingVelocityYoVariableTest.class,
   us.ihmc.robotics.math.filters.BacklashProcessingYoVariableTest.class,
   us.ihmc.robotics.math.filters.BetaFilteredYoVariableTest.class,
   us.ihmc.robotics.math.filters.DeadzoneYoVariableTest.class,
   us.ihmc.robotics.math.filters.DelayedBooleanYoVariableTest.class,
   us.ihmc.robotics.math.filters.DelayedDoubleYoVariableTest.class,
   us.ihmc.robotics.math.filters.DeltaLimitedYoVariableTest.class,
   us.ihmc.robotics.math.filters.FilteredDiscreteVelocityYoVariableTest.class,
   us.ihmc.robotics.math.filters.FilteredVelocityYoVariableTest.class,
   us.ihmc.robotics.math.filters.FirstOrderFilteredYoVariableTest.class,
   us.ihmc.robotics.math.filters.GlitchFilteredBooleanYoVariableTest.class,
   us.ihmc.robotics.math.filters.HysteresisFilteredYoVariableTest.class,
   us.ihmc.robotics.math.filters.RateLimitedYoVariableTest.class,
   us.ihmc.robotics.math.filters.SimpleMovingAverageFilteredYoVariableTest.class,
   us.ihmc.robotics.math.frames.YoFramePointInMultipleFramesTest.class,
   us.ihmc.robotics.math.frames.YoFrameQuaternionTest.class,
   us.ihmc.robotics.math.frames.YoMatrixTest.class,
   us.ihmc.robotics.math.frames.YoMultipleFramesHelperTest.class,
   us.ihmc.robotics.math.interpolators.OrientationInterpolationCalculatorTest.class,
   us.ihmc.robotics.math.interpolators.QuinticSplineInterpolatorTest.class,
   us.ihmc.robotics.math.MatrixYoVariableConversionToolsTest.class,
   us.ihmc.robotics.math.QuaternionCalculusTest.class,
   us.ihmc.robotics.math.TimestampedVelocityYoVariableTest.class,
   us.ihmc.robotics.math.trajectories.CirclePositionTrajectoryGeneratorTest.class,
   us.ihmc.robotics.math.trajectories.ConstantAccelerationTrajectoryGeneratorTest.class,
   us.ihmc.robotics.math.trajectories.ConstantForceTrajectoryGeneratorTest.class,
   us.ihmc.robotics.math.trajectories.ConstantOrientationTrajectoryGeneratorTest.class,
   us.ihmc.robotics.math.trajectories.ConstantPoseTrajectoryGeneratorTest.class,
   us.ihmc.robotics.math.trajectories.ConstantPositionTrajectoryGeneratorTest.class,
   us.ihmc.robotics.math.trajectories.ConstantVelocityTrajectoryGeneratorTest.class,
   us.ihmc.robotics.math.trajectories.HermiteCurveBasedOrientationTrajectoryGeneratorTest.class,
   us.ihmc.robotics.math.trajectories.IntermediateWaypointVelocityGeneratorTest.class,
   us.ihmc.robotics.math.trajectories.OneDoFJointQuinticTrajectoryGeneratorTest.class,
   us.ihmc.robotics.math.trajectories.OrientationInterpolationTrajectoryGeneratorTest.class,
   us.ihmc.robotics.math.trajectories.PositionTrajectorySmootherTest.class,
   us.ihmc.robotics.math.trajectories.ProviderBasedConstantOrientationTrajectoryGeneratorTest.class,
   us.ihmc.robotics.math.trajectories.providers.YoOrientationProviderTest.class,
   us.ihmc.robotics.math.trajectories.providers.YoPositionProviderTest.class,
   us.ihmc.robotics.math.trajectories.providers.YoSE3ConfigurationProviderTest.class,
   us.ihmc.robotics.math.trajectories.providers.YoVariableDoubleProviderTest.class,
   us.ihmc.robotics.math.trajectories.SimpleOrientationTrajectoryGeneratorTest.class,
   us.ihmc.robotics.math.trajectories.StraightLinePositionTrajectoryGeneratorTest.class,
   us.ihmc.robotics.math.trajectories.VelocityConstrainedOrientationTrajectoryGeneratorTest.class,
   us.ihmc.robotics.math.trajectories.waypoints.FrameEuclideanTrajectoryPointTest.class,
   us.ihmc.robotics.math.trajectories.waypoints.FrameSE3TrajectoryPointTest.class,
   us.ihmc.robotics.math.trajectories.waypoints.FrameSO3TrajectoryPointTest.class,
   us.ihmc.robotics.math.trajectories.waypoints.MultipleWaypointsOrientationTrajectoryGeneratorTest.class,
   us.ihmc.robotics.math.trajectories.waypoints.MultipleWaypointsPositionTrajectoryGeneratorTest.class,
   us.ihmc.robotics.math.trajectories.waypoints.MultipleWaypointsTrajectoryGeneratorTest.class,
   us.ihmc.robotics.math.trajectories.waypoints.SimpleEuclideanTrajectoryPointTest.class,
   us.ihmc.robotics.math.trajectories.waypoints.SimpleSE3TrajectoryPointTest.class,
   us.ihmc.robotics.math.trajectories.waypoints.SimpleSO3TrajectoryPointTest.class,
   us.ihmc.robotics.math.trajectories.waypoints.TrajectoryPointOptimizerTest.class,
   us.ihmc.robotics.math.trajectories.waypoints.YoFrameEuclideanTrajectoryPointTest.class,
   us.ihmc.robotics.math.trajectories.waypoints.YoFrameSE3TrajectoryPointTest.class,
   us.ihmc.robotics.math.trajectories.waypoints.YoFrameSO3TrajectoryPointTest.class,
   us.ihmc.robotics.math.trajectories.WrapperForMultiplePositionTrajectoryGeneratorsTest.class,
   us.ihmc.robotics.math.trajectories.WrapperForPositionAndOrientationTrajectoryGeneratorsTest.class,
   us.ihmc.robotics.math.trajectories.YoConcatenatedSplinesTest.class,
   us.ihmc.robotics.math.trajectories.YoMinimumJerkTrajectoryTest.class,
   us.ihmc.robotics.math.trajectories.YoParabolicTrajectoryGeneratorTest.class,
   us.ihmc.robotics.math.trajectories.YoSpline3DTest.class,
   us.ihmc.robotics.math.YoRMSCalculatorTest.class,
   us.ihmc.robotics.math.YoSignalDerivativeTest.class,
   us.ihmc.robotics.MathToolsTest.class,
   us.ihmc.robotics.numericalMethods.DifferentiatorTest.class,
   us.ihmc.robotics.numericalMethods.NewtonRaphsonMethodTest.class,
   us.ihmc.robotics.numericalMethods.QuarticEquationSolverTest.class,
   us.ihmc.robotics.numericalMethods.QuarticRootFinderTest.class,
   us.ihmc.robotics.optimization.ActiveSearchQuadraticProgramOptimizerTest.class,
   us.ihmc.robotics.optimization.EqualityConstraintEnforcerTest.class,
   us.ihmc.robotics.quadTree.QuadTreeForGroundTest.class,
   us.ihmc.robotics.random.RandomToolsTest.class,
   us.ihmc.robotics.referenceFrames.CenterOfMassReferenceFrameTest.class,
   us.ihmc.robotics.referenceFrames.Pose2dReferenceFrameTest.class,
   us.ihmc.robotics.referenceFrames.PoseReferenceFrameTest.class,
   us.ihmc.robotics.referenceFrames.ReferenceFrameTest.class,
   us.ihmc.robotics.robotSide.QuadrantDependentListTest.class,
   us.ihmc.robotics.robotSide.RecyclingQuadrantDependentListTest.class,
   us.ihmc.robotics.robotSide.RobotQuadrantTest.class,
   us.ihmc.robotics.robotSide.RobotSideTest.class,
   us.ihmc.robotics.robotSide.SideDependentListTest.class,
   us.ihmc.robotics.screwTheory.CenterOfMassAccelerationCalculatorTest.class,
   us.ihmc.robotics.screwTheory.CenterOfMassJacobianTest.class,
   us.ihmc.robotics.screwTheory.CompositeRigidBodyMassMatrixCalculatorTest.class,
   us.ihmc.robotics.screwTheory.ConvectiveTermCalculatorTest.class,
   us.ihmc.robotics.screwTheory.DesiredJointAccelerationCalculatorTest.class,
   us.ihmc.robotics.screwTheory.DifferentialIDMassMatrixCalculatorTest.class,
   us.ihmc.robotics.screwTheory.FourBarKinematicLoopTest.class,
   us.ihmc.robotics.screwTheory.GenericCRC32Test.class,
   us.ihmc.robotics.screwTheory.GeometricJacobianTest.class,
   us.ihmc.robotics.screwTheory.MomentumCalculatorTest.class,
   us.ihmc.robotics.screwTheory.MomentumTest.class,
   us.ihmc.robotics.screwTheory.PassiveRevoluteJointTest.class,
   us.ihmc.robotics.screwTheory.PointJacobianTest.class,
   us.ihmc.robotics.screwTheory.RigidBodyInertiaTest.class,
   us.ihmc.robotics.screwTheory.ScrewToolsTest.class,
   us.ihmc.robotics.screwTheory.SpatialAccelerationVectorTest.class,
   us.ihmc.robotics.screwTheory.SpatialMotionVectorTest.class,
   us.ihmc.robotics.screwTheory.ThreeDoFAngularAccelerationCalculatorTest.class,
   us.ihmc.robotics.screwTheory.TotalMassCalculatorTest.class,
   us.ihmc.robotics.screwTheory.TwistTest.class,
   us.ihmc.robotics.screwTheory.WrenchTest.class,
   us.ihmc.robotics.stateMachines.StateChangeRecorderTest.class,
   us.ihmc.robotics.stateMachines.StateMachineExampleOneTest.class,
   us.ihmc.robotics.stateMachines.StateMachineExampleTwoTest.class,
   us.ihmc.robotics.stateMachines.StateMachineTest.class,
   us.ihmc.robotics.stateMachines.SimpleStateTest.class,
   us.ihmc.robotics.statistics.CovarianceDerivationTest.class,
   us.ihmc.robotics.statistics.OnePassMeanAndStandardDeviationTest.class,
   us.ihmc.robotics.statistics.PermutationTest.class,
   us.ihmc.robotics.time.CallFrequencyCalculatorTest.class,
   us.ihmc.robotics.time.ExecutionTimerTest.class,
   us.ihmc.robotics.time.GlobalTimerTest.class,
   us.ihmc.robotics.time.TimeToolsTest.class,
   us.ihmc.robotics.trajectories.LinearInterpolatorTest.class,
   us.ihmc.robotics.trajectories.MinimumJerkTrajectoryTest.class,
   us.ihmc.robotics.trajectories.ParametricSplineTrajectorySolverTest.class,
   us.ihmc.robotics.trajectories.PolynomialSplineTest.class,
   us.ihmc.robotics.trajectories.providers.ConstantDoubleProviderTest.class,
   us.ihmc.robotics.trajectories.providers.ConstantPositionProviderTest.class,
   us.ihmc.robotics.trajectories.providers.CurrentPositionProviderTest.class,
   us.ihmc.robotics.trajectories.TrapezoidalVelocityTrajectoryTest.class,
   us.ihmc.robotics.trajectories.WaypointMotionGeneratorTest.class
})

public class IHMCRoboticsToolkitAFastTestSuite
{
   public static void main(String[] args)
   {

   }
}
