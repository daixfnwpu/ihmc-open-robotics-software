package move_arm_head_monitor;

public interface HeadMonitorGoal extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "move_arm_head_monitor/HeadMonitorGoal";
  static final java.lang.String _DEFINITION = "# ====== DO NOT MODIFY! AUTOGENERATED FROM AN ACTION DEFINITION ======\n#goal definition\ntime stop_time\nfloat32 max_frequency\nduration time_offset\nstring target_link\nfloat32 target_x\nfloat32 target_y\nfloat32 target_z\n";
  org.ros.message.Time getStopTime();
  void setStopTime(org.ros.message.Time value);
  float getMaxFrequency();
  void setMaxFrequency(float value);
  org.ros.message.Duration getTimeOffset();
  void setTimeOffset(org.ros.message.Duration value);
  java.lang.String getTargetLink();
  void setTargetLink(java.lang.String value);
  float getTargetX();
  void setTargetX(float value);
  float getTargetY();
  void setTargetY(float value);
  float getTargetZ();
  void setTargetZ(float value);
}
