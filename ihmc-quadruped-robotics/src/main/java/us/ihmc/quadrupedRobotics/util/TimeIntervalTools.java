package us.ihmc.quadrupedRobotics.util;

import java.util.Comparator;
import java.util.List;

import us.ihmc.tools.lists.ListSorter;

@SuppressWarnings("unchecked")
public class TimeIntervalTools
{
   static public void sortByStartTime(List<? extends TimeIntervalProvider> timeIntervalProviders)
   {
      ListSorter.sort((List<TimeIntervalProvider>)timeIntervalProviders, startTimeComparator);
   }

   static public void sortByReverseStartTime(List<? extends TimeIntervalProvider> timeIntervalProviders)
   {
      ListSorter.sort((List<TimeIntervalProvider>)timeIntervalProviders, startTimeComparator.reversed());
   }

   static public void sortByEndTime(List<? extends TimeIntervalProvider> timeIntervalProviders)
   {
      ListSorter.sort((List<TimeIntervalProvider>)timeIntervalProviders, endTimeComparator);
   }

   static public void sortByReverseEndTime(List<? extends TimeIntervalProvider> timeIntervalProviders)
   {
      ListSorter.sort((List<TimeIntervalProvider>)timeIntervalProviders, endTimeComparator.reversed());
   }

   static public void removeStartTimesLessThan(double time, List<? extends TimeIntervalProvider> timeIntervalProviders)
   {
      for (int i = 0; i < timeIntervalProviders.size(); i++)
      {
         if (timeIntervalProviders.get(i).getTimeInterval().getStartTime() < time)
         {
            timeIntervalProviders.remove(i);
            i--;
         }
      }
   }

   static public void removeStartTimesLessThanOrEqualTo(double time, List<? extends TimeIntervalProvider> timeIntervalProviders)
   {
      for (int i = 0; i < timeIntervalProviders.size(); i++)
      {
         if (timeIntervalProviders.get(i).getTimeInterval().getStartTime() <= time)
         {
            timeIntervalProviders.remove(i);
            i--;
         }
      }
   }

   static public void removeStartTimesGreaterThan(double time, List<? extends TimeIntervalProvider> timeIntervalProviders)
   {
      for (int i = timeIntervalProviders.size() - 1; i >= 0; i--)
      {
         if (timeIntervalProviders.get(i).getTimeInterval().getStartTime() > time)
         {
            timeIntervalProviders.remove(i);
         }
      }
   }

   static public void removeStartTimesGreaterThanOrEqualTo(double time, List<? extends TimeIntervalProvider> timeIntervalProviders)
   {
      for (int i = timeIntervalProviders.size() - 1; i >= 0; i--)
      {
         if (timeIntervalProviders.get(i).getTimeInterval().getStartTime() >= time)
         {
            timeIntervalProviders.remove(i);
         }
      }
   }

   static public void removeEndTimesLessThan(double time, List<? extends TimeIntervalProvider> timeIntervalProviders)
   {
      for (int i = 0; i < timeIntervalProviders.size(); i++)
      {
         if (timeIntervalProviders.get(i).getTimeInterval().getEndTime() < time)
         {
            timeIntervalProviders.remove(i);
            i--;
         }
      }
   }

   static public void removeEndTimesLessThanOrEqualTo(double time, List<? extends TimeIntervalProvider> timeIntervalProviders)
   {
      for (int i = 0; i < timeIntervalProviders.size(); i++)
      {
         if (timeIntervalProviders.get(i).getTimeInterval().getEndTime() <= time)
         {
            timeIntervalProviders.remove(i);
            i--;
         }
      }
   }

   static public void removeEndTimesGreaterThan(double time, List<? extends TimeIntervalProvider> timeIntervalProviders)
   {
      for (int i = timeIntervalProviders.size() - 1; i >= 0; i--)
      {
         if (timeIntervalProviders.get(i).getTimeInterval().getEndTime() > time)
         {
            timeIntervalProviders.remove(i);
         }
      }
   }

   static public void removeEndTimesGreaterThanOrEqualTo(double time, List<? extends TimeIntervalProvider> timeIntervalProviders)
   {
      for (int i = timeIntervalProviders.size() - 1; i >= 0; i--)
      {
         if (timeIntervalProviders.get(i).getTimeInterval().getEndTime() >= time)
         {
            timeIntervalProviders.remove(i);
         }
      }
   }

   public static Comparator<TimeIntervalProvider> startTimeComparator = (TimeIntervalProvider a, TimeIntervalProvider b) -> {
      double startTimeA = a.getTimeInterval().getStartTime();
      double startTimeB = b.getTimeInterval().getStartTime();
      return Double.compare(startTimeA, startTimeB);
   };

   public static Comparator<TimeIntervalProvider> endTimeComparator = (TimeIntervalProvider a, TimeIntervalProvider b) -> {
      double endTimeA = a.getTimeInterval().getEndTime();
      double endTimeB = b.getTimeInterval().getEndTime();
      return Double.compare(endTimeA, endTimeB);
   };
}
