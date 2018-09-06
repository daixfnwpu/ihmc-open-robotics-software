package controller_msgs.msg.dds;

/**
* 
* Topic data type of the struct "Duration" defined in "Duration_.idl". Use this class to provide the TopicDataType to a Participant. 
*
* This file was automatically generated from Duration_.idl by us.ihmc.idl.generator.IDLGenerator. 
* Do not update this file directly, edit Duration_.idl instead.
*
*/
public class DurationPubSubType implements us.ihmc.pubsub.TopicDataType<controller_msgs.msg.dds.Duration>
{
   public static final java.lang.String name = "controller_msgs::msg::dds_::Duration_";

   private final us.ihmc.idl.CDR serializeCDR = new us.ihmc.idl.CDR();
   private final us.ihmc.idl.CDR deserializeCDR = new us.ihmc.idl.CDR();

   @Override
   public void serialize(controller_msgs.msg.dds.Duration data, us.ihmc.pubsub.common.SerializedPayload serializedPayload) throws java.io.IOException
   {
      serializeCDR.serialize(serializedPayload);
      write(data, serializeCDR);
      serializeCDR.finishSerialize();
   }

   @Override
   public void deserialize(us.ihmc.pubsub.common.SerializedPayload serializedPayload, controller_msgs.msg.dds.Duration data) throws java.io.IOException
   {
      deserializeCDR.deserialize(serializedPayload);
      read(data, deserializeCDR);
      deserializeCDR.finishDeserialize();
   }

   public static int getMaxCdrSerializedSize()
   {
      return getMaxCdrSerializedSize(0);
   }

   public static int getMaxCdrSerializedSize(int current_alignment)
   {
      int initial_alignment = current_alignment;

      current_alignment += 4 + us.ihmc.idl.CDR.alignment(current_alignment, 4);

      current_alignment += 4 + us.ihmc.idl.CDR.alignment(current_alignment, 4);


      return current_alignment - initial_alignment;
   }

   public final static int getCdrSerializedSize(controller_msgs.msg.dds.Duration data)
   {
      return getCdrSerializedSize(data, 0);
   }

   public final static int getCdrSerializedSize(controller_msgs.msg.dds.Duration data, int current_alignment)
   {
      int initial_alignment = current_alignment;

      current_alignment += 4 + us.ihmc.idl.CDR.alignment(current_alignment, 4);


      current_alignment += 4 + us.ihmc.idl.CDR.alignment(current_alignment, 4);



      return current_alignment - initial_alignment;
   }

   public static void write(controller_msgs.msg.dds.Duration data, us.ihmc.idl.CDR cdr)
   {
      cdr.write_type_2(data.getSec());

      cdr.write_type_4(data.getNanosec());

   }

   public static void read(controller_msgs.msg.dds.Duration data, us.ihmc.idl.CDR cdr)
   {
      data.setSec(cdr.read_type_2());
      	
      data.setNanosec(cdr.read_type_4());
      	

   }

   @Override
   public final void serialize(controller_msgs.msg.dds.Duration data, us.ihmc.idl.InterchangeSerializer ser)
   {
      ser.write_type_2("sec", data.getSec());
      ser.write_type_4("nanosec", data.getNanosec());
   }

   @Override
   public final void deserialize(us.ihmc.idl.InterchangeSerializer ser, controller_msgs.msg.dds.Duration data)
   {
      data.setSec(ser.read_type_2("sec"));
      data.setNanosec(ser.read_type_4("nanosec"));
   }

   public static void staticCopy(controller_msgs.msg.dds.Duration src, controller_msgs.msg.dds.Duration dest)
   {
      dest.set(src);
   }

   @Override
   public controller_msgs.msg.dds.Duration createData()
   {
      return new controller_msgs.msg.dds.Duration();
   }
   @Override
   public int getTypeSize()
   {
      return us.ihmc.idl.CDR.getTypeSize(getMaxCdrSerializedSize());
   }

   @Override
   public java.lang.String getName()
   {
      return name;
   }
   
   public void serialize(controller_msgs.msg.dds.Duration data, us.ihmc.idl.CDR cdr)
   {
      write(data, cdr);
   }

   public void deserialize(controller_msgs.msg.dds.Duration data, us.ihmc.idl.CDR cdr)
   {
      read(data, cdr);
   }
   
   public void copy(controller_msgs.msg.dds.Duration src, controller_msgs.msg.dds.Duration dest)
   {
      staticCopy(src, dest);
   }

   @Override
   public DurationPubSubType newInstance()
   {
      return new DurationPubSubType();
   }
}