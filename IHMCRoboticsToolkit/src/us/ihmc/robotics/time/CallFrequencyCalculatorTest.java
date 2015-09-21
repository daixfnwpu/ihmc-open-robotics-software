package us.ihmc.robotics.time;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.Test;

import us.ihmc.robotics.dataStructures.registry.YoVariableRegistry;
import us.ihmc.tools.testing.TestPlanAnnotations.DeployableTestMethod;

public class CallFrequencyCalculatorTest
{
   @DeployableTestMethod(estimatedDuration = 0.0)
   @Test(timeout = 30000)
   public void testDetermineCallFrequency()
   {
      CallFrequencyCalculator callFrequencyCalculator = new CallFrequencyCalculator(new YoVariableRegistry("test"), "");

      int desiredFreq = 20;
      long delay = (long) (1000.0 / desiredFreq);

      for (int i = 0; i < 100; i++)
      {
         try
         {
            Thread.sleep(delay);
         }
         catch (InterruptedException e)
         {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }

         double freq = callFrequencyCalculator.determineCallFrequency();

         if (freq != 0.0)
         {
            assertEquals(desiredFreq, freq, 1e-2);
         }
      }
   }

}
